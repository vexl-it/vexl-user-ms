package com.cleevio.vexl.module.user.entity;

import com.cleevio.vexl.common.convertor.AesEncryptionConvertor;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToOne;
import javax.persistence.Table;

@Entity
@Table(name = "users")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor(access = AccessLevel.PACKAGE)
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@ToString(onlyExplicitlyIncluded = true)
public class User {

    @Id
    @EqualsAndHashCode.Include
    @ToString.Include
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @Convert(converter = AesEncryptionConvertor.class)
    private String username;

    @Column
    @Convert(converter = AesEncryptionConvertor.class)
    private String avatar;

    @Column(name = "public_key")
    private byte[] publicKey;

    @OneToOne(mappedBy = "user", fetch = FetchType.LAZY)
    private UserVerification userVerification;

    public User(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.avatar = user.getAvatar();
        this.publicKey = user.getPublicKey();
    }
}
