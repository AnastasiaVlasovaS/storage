import com.example.cloud.storage.AuthFilter;
import com.example.cloud.storage.LoginRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import java.util.Map;
import java.util.HashMap;
import java.util.UUID;

@PostMapping("/login")
public Map<String, String> login(@RequestBody LoginRequest request) {
    // Проверка логина и пароля
    if ("testuser".equals(request.getLogin()) && "1234".equals(request.getPassword())) {
        String token = UUID.randomUUID().toString();

        AuthFilter.addToken(token);

        Map<String, String> response = new HashMap<>();
        response.put("auth-token", token);
        return response;
    } else {
        throw new RuntimeException("Неверный логин или пароль");
    }
}

void main() {
}
