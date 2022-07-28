package com.cleevio.vexl.module.file.event.listener;

import com.cleevio.vexl.module.file.service.ImageService;
import com.cleevio.vexl.module.user.event.UserRemovedEvent;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.validation.annotation.Validated;

import javax.validation.Valid;

@Component
@Validated
@RequiredArgsConstructor
class UserFileEventListener {

    private final ImageService imageService;

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onUserRemovedEvent(@Valid final UserRemovedEvent event) {
        this.imageService.removeAvatar(event.avatar());
    }
}
