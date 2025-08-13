package example.config;


import example.entity.User;
import example.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {

    @Autowired
    private UserRepository userRepository;

    @Override
    public void run(String... args) throws Exception {
        // Load some sample data
        userRepository.save(new User("John Doe", "john.doe@example.com"));
        userRepository.save(new User("Jane Smith", "jane.smith@example.com"));
        userRepository.save(new User("Bob Johnson", "bob.johnson@example.com"));
    }
}