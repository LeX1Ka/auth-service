package org.example.authservice.service;

import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.example.authservice.entity.VerificationCode;
import org.example.authservice.repository.VerificationCodeRepository;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class EmailVerificationService {
    private final VerificationCodeRepository verificationCodeRepository;
    private final JavaMailSender mailSender;

    public void sendVerificationCode(String email) {
        String code = String.format("%06d", new Random().nextInt(1_000_000));

        VerificationCode verificationCode = verificationCodeRepository
                .findByEmail(email)
                .orElse(new VerificationCode());

        verificationCode.setEmail(email);
        verificationCode.setVerificationCode(code);
        verificationCode.setExpiresAt(LocalDateTime.now().plusMinutes(15));
        verificationCode.setConfirmed(false);

        verificationCodeRepository.save(verificationCode);

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);
            helper.setTo(email);
            helper.setSubject("Код подтверждения регистрации");
            helper.setText("Ваш код подтверждения: " + code, true);
            mailSender.send(message);
            System.out.println("Код отправлен на email: " + email);
        } catch (Exception e) {
            System.err.println("Ошибка при отправке письма: " + e.getMessage());
        }
    }



    public boolean verify(String email, String code) {
        Optional<VerificationCode> optional = verificationCodeRepository.findByEmail(email);

        if (optional.isEmpty()) {
            return false;
        }

        VerificationCode verificationCode = optional.get();

        if (verificationCode.isConfirmed()) {
            return false;
        }

        if (!verificationCode.getVerificationCode().equals(code)) {
            return false;
        }

        if (verificationCode.getExpiresAt().isBefore(LocalDateTime.now())) {
            return false;
        }

        verificationCode.setConfirmed(true);
        verificationCodeRepository.save(verificationCode);

        return true;
    }

    @Scheduled(fixedRate = 60_000)
    public void deleteExpiredCodes() {
        verificationCodeRepository.deleteAllByExpiresAtBefore(LocalDateTime.now());
    }


}
