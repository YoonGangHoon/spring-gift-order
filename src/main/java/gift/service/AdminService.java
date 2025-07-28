package gift.service;

import gift.dto.MemberLoginRequestDto;
import gift.entity.Admin;
import gift.exception.InvalidPasswordException;
import gift.exception.MemberNotFoundException;
import gift.jwt.JwtProvider;
import gift.repository.AdminRepository;
import jakarta.validation.Valid;
import org.springframework.stereotype.Service;

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final JwtProvider jwtProvider;
    public AdminService(AdminRepository adminRepository, JwtProvider jwtProvider) {
        this.adminRepository = adminRepository;
        this.jwtProvider = jwtProvider;
    }

    public String login(@Valid MemberLoginRequestDto requestDto) {
        Admin existed = adminRepository.findByEmail(requestDto.email())
                .orElseThrow(() -> new MemberNotFoundException("email", requestDto.email()));

        if (existed.getPassword().equals(requestDto.password())) {
             return jwtProvider.generateToken(existed);
        }
        else{
            throw new InvalidPasswordException();
        }
    }
}
