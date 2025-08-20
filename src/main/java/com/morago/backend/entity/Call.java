package com.morago.backend.entity;

import com.morago.backend.entity.enumFiles.CallStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.math.BigDecimal;

@Entity
@Table(name = "calls")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
public class Call {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "caller_id", nullable = false)
    private User caller;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "theme_id")
    private Theme theme;

    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "duration", nullable = false)
    @Builder.Default
    @PositiveOrZero
    private int duration = 0;

    @Column(name = "status", nullable = false)   // либо 'successful'
    @Builder.Default
    private boolean status = false;

    @PositiveOrZero
    @Column(name = "sum_decimal", precision = 10, scale = 2)
    private BigDecimal sumDecimal;

    @PositiveOrZero
    @Column(name = "commission", precision = 10, scale = 2)
    private BigDecimal commission;

    @Column(name = "translator_has_joined", nullable = false)
    @Builder.Default
    private boolean translatorHasJoined = false;

    @Column(name = "user_has_rated", nullable = false)
    @Builder.Default
    private boolean userHasRated = false;

    @LastModifiedDate
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    @Column(name = "channel_name", length = 50)
    private String channelName;

    @Enumerated(EnumType.STRING)
    @Column(name = "call_status", length = 40, nullable = false)
    @Builder.Default
    private CallStatus callStatus = CallStatus.CONNECT_NOT_SET;

    @Column(name = "is_end_call")
    @Builder.Default
    private boolean isEndCall = false;
}

