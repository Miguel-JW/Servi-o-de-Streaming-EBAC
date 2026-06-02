import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UsuarioService {

    private final UsuarioRepository     usuarioRepository;
    private final JwtService            jwtService;
    private final BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

    public UsuarioService(UsuarioRepository usuarioRepository, JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.jwtService        = jwtService;
    }

    // ── Cadastro ───────────────────────────────────────────
    public Usuario cadastrar(CadastroRequest req) {
        if (usuarioRepository.existsByEmail(req.email()))
            throw new EmailJaCadastradoException(req.email());

        String senhaCriptografada = encoder.encode(req.senha());
        Usuario usuario = new Usuario(req.nome(), req.email(), senhaCriptografada, req.plano());
        return usuarioRepository.save(usuario);
    }

    // ── Login ──────────────────────────────────────────────
    public String login(LoginRequest req) {
        Usuario usuario = usuarioRepository.findByEmail(req.email())
            .orElseThrow(() -> new CredenciaisInvalidasException());

        if (usuario.getStatus() == StatusConta.SUSPENSA)
            throw new ContaSuspensaException();

        if (usuario.getStatus() == StatusConta.CANCELADA)
            throw new ContaCanceladaException();

        if (!encoder.matches(req.senha(), usuario.getSenha()))
            throw new CredenciaisInvalidasException();

        usuario.setUltimoLogin(LocalDateTime.now());
        usuarioRepository.save(usuario);

        return jwtService.gerarToken(usuario.getEmail());
    }

    // ── Busca por ID ───────────────────────────────────────
    public Usuario buscarPorId(Long id) {
        return usuarioRepository.findById(id)
            .orElseThrow(() -> new UsuarioNaoEncontradoException(id));
    }

    // ── Lista todos ────────────────────────────────────────
    public List<Usuario> listar() {
        return usuarioRepository.findAll();
    }

    // ── Atualiza plano ─────────────────────────────────────
    public Usuario atualizarPlano(Long id, Plano novoPlano) {
        Usuario usuario = buscarPorId(id);
        usuario.setPlano(novoPlano);
        return usuarioRepository.save(usuario);
    }

    // ── Suspende conta ─────────────────────────────────────
    public void suspender(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setStatus(StatusConta.SUSPENSA);
        usuarioRepository.save(usuario);
    }

    // ── Cancela conta ──────────────────────────────────────
    public void cancelar(Long id) {
        Usuario usuario = buscarPorId(id);
        usuario.setStatus(StatusConta.CANCELADA);
        usuarioRepository.save(usuario);
    }
}
