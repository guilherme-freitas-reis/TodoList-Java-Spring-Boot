package br.dev.guilhermereis.todolist.user;

import at.favre.lib.crypto.bcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController()
@RequestMapping("/users")
public class UserController {
    @Autowired
    private IUserRepository userRepository;


    @GetMapping("/")
    public ResponseEntity findAll() {
        var users = this.userRepository.findAll();
        users.forEach(userModel -> userModel.setPassword(""));

        return ResponseEntity.status(HttpStatus.OK).body(users);
    }

    @PostMapping("/")
    public ResponseEntity create(@RequestBody UserModel userModel) {
        var user = this.userRepository.findByUsername(userModel.getUsername());

        if(user != null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Username já existe");
        }

        var passwordHashed = BCrypt.withDefaults().hashToString(12, userModel.getPassword().toCharArray());
        userModel.setPassword(passwordHashed);

        var createdUser = this.userRepository.save(userModel);
        createdUser.setPassword("");

        return ResponseEntity.status(HttpStatus.CREATED).body(createdUser);
    }
}
