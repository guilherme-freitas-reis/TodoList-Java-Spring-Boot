package br.dev.guilhermereis.todolist.task;

import br.dev.guilhermereis.todolist.user.UserModel;
import br.dev.guilhermereis.todolist.utils.Utils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.config.Task;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/tasks")
public class TaskController {
    @Autowired
    private ITaskRepository taskRepository;


    @GetMapping("/")
    public ResponseEntity findAll(HttpServletRequest request) {
        var user = UserModel.class.cast(request.getAttribute("user")) ;

        var tasks = this.taskRepository.findByIdUser(user.getId());

        return ResponseEntity.status(HttpStatus.OK).body(tasks);
    }

    @PostMapping("/")
    public ResponseEntity create(@RequestBody TaskModel taskModel, HttpServletRequest request) {
        var user = UserModel.class.cast(request.getAttribute("user")) ;
        taskModel.setIdUser(user.getId());

        var currentDate = LocalDateTime.now();
        if(currentDate.isAfter(taskModel.getStartAt()) || currentDate.isAfter(taskModel.getEndAt()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("As datas de inicio/término devem ser maior que a data atual.");

        if(taskModel.getStartAt().isAfter(taskModel.getEndAt()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("A data término deve ser maior que a data de inicio.");

        var task = this.taskRepository.save(taskModel);
        return  ResponseEntity.status(HttpStatus.CREATED).body(task);
    }

    @PutMapping("/{id}")
    public ResponseEntity update(@RequestBody TaskModel taskModel, @PathVariable UUID id, HttpServletRequest request) {
        var user = UserModel.class.cast(request.getAttribute("user")) ;

        var task = this.taskRepository.findById(id).orElse(null);
        if(task == null)
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("");

        if(user.getId() != task.getIdUser())
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("O usuário não tem permissão para alterar esta task.");

        Utils.copyNonNullProperties(taskModel, task);

        var updatedTask = this.taskRepository.save(task);
        return  ResponseEntity.status(HttpStatus.OK).body(updatedTask);
    }
}
