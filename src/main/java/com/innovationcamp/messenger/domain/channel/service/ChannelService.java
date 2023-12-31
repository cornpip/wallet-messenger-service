package com.innovationcamp.messenger.domain.channel.service;

import com.innovationcamp.messenger.domain.channel.config.ChannelPasswordEncoder;
import com.innovationcamp.messenger.domain.channel.dto.*;
import com.innovationcamp.messenger.domain.channel.entity.Channel;
import com.innovationcamp.messenger.domain.channel.entity.UserChannel;
import com.innovationcamp.messenger.domain.channel.repository.ChannelRepository;
import com.innovationcamp.messenger.domain.channel.repository.UserChannelRepository;
import com.innovationcamp.messenger.domain.user.entity.User;
import com.innovationcamp.messenger.domain.user.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChannelService {
    @NonNull
    private final ChannelRepository channelRepository;
    @NonNull
    private final UserRepository userRepository;
    @NonNull
    private final UserChannelRepository userChannelRepository;
    @NonNull
    private final ChannelPasswordEncoder pwEncoder;

    @Transactional
    public CreateChannelResponseDto createChannel(User user, CreateChannelRequestDto requestDto
    ) {
        if (requestDto.getIsPrivate() == null) {
            requestDto.setIsPrivate(false);
        }
        Channel channel = Channel.builder()
                .channelName(requestDto.getChannelName())
                .channelCreateUser(user)
                .channelPassword(pwEncoder.encode(requestDto.getChannelPassword()))
                .channelDescription(requestDto.getChannelDescription())
                .isPrivate(requestDto.getIsPrivate())
                .build();
        channelRepository.save(channel);

        UserChannel userChannel = UserChannel.builder()
                .user(user)
                .channel(channel)
                .isAdmin(true)
                .build();
        userChannelRepository.save(userChannel);

        return new CreateChannelResponseDto(channel);
    }

    public GetChannelResponseDto getChannel(Long id, User user) {
        userChannelRepository.findByUserAndChannelId(user, id)
                .orElseThrow(() -> new EntityNotFoundException("참여하지 않은 채널입니다."));

        Channel channel = channelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Channel not found"));

        return new GetChannelResponseDto(channel.getId(),
                channel.getChannelName(),
                channel.getChannelCreateUser().getUsername(),
                channel.getChannelDescription(),
                channel.getCreatedAt(),
                channel.getIsPrivate(),
                channelRepository.countUserInChannel(channel.getId()));
    }

    public SignUpChannelResponseDto signUpChannel(Long channelId, String channelpw, User user) {
        Channel channel = channelRepository.findById(channelId).orElseThrow(() -> new IllegalArgumentException("존재하지 않는 채널입니다."));
        if (userChannelRepository.findByUserAndChannel(user, channel).isPresent())
            throw new IllegalArgumentException("이미 가입한 채널입니다.");
        if (channel.getIsPrivate()) {
            if (channelpw == null) throw new IllegalArgumentException("해당 채널은 비밀 채널입니다. 비밀번호를 입력해주세요.");
            if (!pwEncoder.checkPassword(channelpw, channel.getChannelPassword())) {
                throw new IllegalArgumentException("비밀 채널에 입장하기 위한 비밀번호가 일치하지 않습니다.");
            }
        }
        UserChannel userChannel = UserChannel.builder()
                .channel(channel)
                .user(user)
                .isAdmin(false)
                .build();
        userChannelRepository.save(userChannel);

        return new SignUpChannelResponseDto(userChannel);
    }

    // search channel by channel name
    public List<GetChannelResponseDto> searchChannel(String channelName) {
        List<Channel> channels = channelRepository.findAllByChannelNameContainingIgnoreCase(channelName);
        List<GetChannelResponseDto> dtoList = channels.stream()
                .map(channel -> new GetChannelResponseDto(channel.getId(),
                        channel.getChannelCreateUser().getUsername(),
                        channel.getChannelName(),
                        channel.getChannelDescription(),
                        channel.getCreatedAt(),
                        channel.getIsPrivate(),
                        channelRepository.countUserInChannel(channel.getId())))
                .collect(Collectors.toList());
        return dtoList;
    }

    public List<GetAllChannelUserInResponseDto> getAllChannelUserIn(User user) {

        List<UserChannel> userChannels = userChannelRepository.findByUser(user);

        List<GetAllChannelUserInResponseDto> dtoList = userChannels.stream()
                .map(userChannel -> {
                    Channel channel = userChannel.getChannel();
                    return new GetAllChannelUserInResponseDto(
                            channel.getId(),
                            channel.getChannelName(),
                            userChannel.getReadTimestamp(),
                            userChannel.isAdmin());
                })
                .collect(Collectors.toList());
        return dtoList;
    }

    @Transactional
    // 비밀번호 수정 기능 없음
    public UpdateChannelResponseDto updateChannel(Long channelId, UpdateChannelRequestDto dto, User user) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new EntityNotFoundException("Channel not found"));
        checkChannelAdmin(channel, user);
        channel.updateChannel(dto);
        return new UpdateChannelResponseDto(channel.getId(), channel.getChannelName(), channel.getChannelDescription());
    }

    @Transactional
    public void deleteChannel(Long channelId, User user) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new EntityNotFoundException("Channel not found"));
        checkChannelAdmin(channel, user);

        List<UserChannel> userChannels = userChannelRepository.findByChannel(channel);
        userChannelRepository.deleteAll(userChannels);

        channelRepository.deleteById(channelId);
    }

    public void addUserToChannel(Long channelId, Long otherUserId, User user) {

        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new EntityNotFoundException("Channel not found"));

        checkUserInChannel(channel, user);

        User otherUser = userRepository.findById(otherUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        UserChannel otherUserAddedChannel = UserChannel.builder()
                .user(otherUser)
                .channel(channel)
                .isAdmin(false)
                .build();

        userChannelRepository.save(otherUserAddedChannel);
    }

    public void kickUserFromChannel(Long channelId, Long otherUserId, User user) {
        Channel channel = channelRepository.findById(channelId)
                .orElseThrow(() -> new EntityNotFoundException("Channel not found"));
        checkChannelAdmin(channel, user);

        User otherUser = userRepository.findById(otherUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));

        userChannelRepository.deleteByUserAndChannel(otherUser, channel);
    }

    // 수정, 삭제 시 Admin 권한 확인
    private void checkChannelAdmin(Channel channel, User user) {
        Optional<UserChannel> userChannel = userChannelRepository.findByUserAndChannel(user, channel);

        if (userChannel.isEmpty() || !userChannel.get().isAdmin()) {
            throw new IllegalArgumentException("현재 사용자는 채널 관리자가 아닙니다.");
        }
    }

    // 사용자가 채널에 속해 있는지 확인
    private void checkUserInChannel(Channel channel, User user) {
        Optional<UserChannel> userChannel = userChannelRepository.findByUserAndChannel(user, channel);

        if (userChannel.isEmpty()) {
            throw new IllegalArgumentException("현재 사용자는 해당 채널에 속해있지 않습니다.");
        }
    }
}
