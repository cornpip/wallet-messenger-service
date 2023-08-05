package com.innovationcamp.messenger.domain.channel.entity;

import com.innovationcamp.messenger.domain.user.entity.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
public class UserChannel{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // UserChannel이 주인인 단방향관계
    @ManyToOne
    @JoinColumn(name = "user_id",nullable = false)
    private User user;
    
    // UserChannel이 주인인 단방향관계
    @ManyToOne
    @JoinColumn(name="channel_id", nullable=false)
    private Channel channel;

    private LocalDateTime readTimestamp;

    private boolean isAdmin;
}
