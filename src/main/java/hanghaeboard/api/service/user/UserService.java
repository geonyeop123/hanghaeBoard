package hanghaeboard.api.service.user;

import hanghaeboard.api.controller.user.request.CreateUserRequest;
import hanghaeboard.api.controller.user.request.LoginRequest;
import hanghaeboard.api.exception.exception.InvalidPasswordException;
import hanghaeboard.api.service.user.response.FindUser;
import hanghaeboard.api.service.user.response.LoginResponse;
import hanghaeboard.domain.user.User;
import hanghaeboard.domain.user.UserRepository;
import hanghaeboard.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;

    private final JwtUtil jwtUtil;


    @Transactional
    public FindUser join(CreateUserRequest request) {

        Optional<User> findUser = userRepository.findByUsername(request.getUsername());

        if(findUser.isPresent()){
            throw new DuplicateKeyException("이미 존재하는 ID입니다.");
        }

        User savedUser = userRepository.save(request.toEntity());

        return FindUser.from(savedUser);
    }

    public FindUser findUserById(Long id){
        User findUser = userRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("일치하는 회원이 없습니다."));

        return FindUser.from(findUser);
    }

    public LoginResponse login(LoginRequest request) {

        User findUser = userRepository.findByUsername(request.getUsername())
                .orElseThrow( () -> new IllegalArgumentException("일치하는 회원이 없습니다."));

        if(!findUser.isCorrectPassword(request.getPassword())){
            throw new InvalidPasswordException("비밀번호가 올바르지 않습니다.");
        }

        String token = jwtUtil.generateToken(request.getUsername());

        return LoginResponse.builder().jwtToken(token).build();
    }

}
