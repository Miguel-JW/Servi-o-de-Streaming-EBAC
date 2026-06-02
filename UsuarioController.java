import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/cadastrar")
    public ResponseEntity<UsuarioResponse> cadastrar(@RequestBody CadastroRequest req) {
        Usuario u = usuarioService.cadastrar(req);
        return ResponseEntity.status(201).body(UsuarioResponse.de(u));
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequest req) {
        String token = usuarioService.login(req);
        return ResponseEntity.ok(token);
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponse>> listar() {
        List<UsuarioResponse> lista = usuarioService.listar()
            .stream().map(UsuarioResponse::de).toList();
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponse> buscar(@PathVariable Long id) {
        return ResponseEntity.ok(UsuarioResponse.de(usuarioService.buscarPorId(id)));
    }

    @PatchMapping("/{id}/plano")
    public ResponseEntity<UsuarioResponse> atualizarPlano(@PathVariable Long id,
                                                          @RequestParam Plano plano) {
        return ResponseEntity.ok(UsuarioResponse.de(usuarioService.atualizarPlano(id, plano)));
    }

    @PatchMapping("/{id}/suspender")
    public ResponseEntity<Void> suspender(@PathVariable Long id) {
        usuarioService.suspender(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        usuarioService.cancelar(id);
        return ResponseEntity.noContent().build();
    }
}
