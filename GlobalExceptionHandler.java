import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UsuarioNaoEncontradoException.class)
    public ResponseEntity<String> notFound(UsuarioNaoEncontradoException e) {
        return ResponseEntity.status(404).body(e.getMessage());
    }

    @ExceptionHandler({EmailJaCadastradoException.class})
    public ResponseEntity<String> conflict(RuntimeException e) {
        return ResponseEntity.status(409).body(e.getMessage());
    }

    @ExceptionHandler({CredenciaisInvalidasException.class,
                       ContaSuspensaException.class,
                       ContaCanceladaException.class})
    public ResponseEntity<String> unauthorized(RuntimeException e) {
        return ResponseEntity.status(401).body(e.getMessage());
    }
}
