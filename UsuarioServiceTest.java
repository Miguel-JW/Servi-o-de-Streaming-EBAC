import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock UsuarioRepository usuarioRepository;
    @Mock JwtService        jwtService;
    @InjectMocks UsuarioService usuarioService;

    // ── TDD: cadastro com email duplicado ──────────────────
    @Test
    void deveLancarExcecaoQuandoEmailJaCadastrado() {
        when(usuarioRepository.existsByEmail("teste@email.com")).thenReturn(true);
        CadastroRequest req = new CadastroRequest("Teste", "teste@email.com", "123456", Plano.BASIC);

        assertThrows(EmailJaCadastradoException.class, () -> usuarioService.cadastrar(req));
        verify(usuarioRepository, never()).save(any());
    }

    // ── TDD: cadastro com sucesso ──────────────────────────
    @Test
    void deveCadastrarUsuarioComSucesso() {
        when(usuarioRepository.existsByEmail(any())).thenReturn(false);
        when(usuarioRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        CadastroRequest req = new CadastroRequest("Miguel", "miguel@email.com", "senha123", Plano.PREMIUM);
        Usuario resultado = usuarioService.cadastrar(req);

        assertEquals("Miguel",           resultado.getNome());
        assertEquals(StatusConta.ATIVA,  resultado.getStatus());
        assertEquals(Plano.PREMIUM,      resultado.getPlano());
    }

    // ── TDD: login com credenciais inválidas ───────────────
    @Test
    void deveLancarExcecaoQuandoEmailNaoExiste() {
        when(usuarioRepository.findByEmail(any())).thenReturn(Optional.empty());
        LoginRequest req = new LoginRequest("naoexiste@email.com", "senha");

        assertThrows(CredenciaisInvalidasException.class, () -> usuarioService.login(req));
    }

    // ── TDD: login com conta suspensa ─────────────────────
    @Test
    void deveLancarExcecaoQuandoContaSuspensa() {
        Usuario u = new Usuario("Ana", "ana@email.com", "hash", Plano.BASIC);
        u.setStatus(StatusConta.SUSPENSA);
        when(usuarioRepository.findByEmail("ana@email.com")).thenReturn(Optional.of(u));

        assertThrows(ContaSuspensaException.class,
            () -> usuarioService.login(new LoginRequest("ana@email.com", "qualquer")));
    }

    // ── TDD: busca por ID inexistente ──────────────────────
    @Test
    void deveLancarExcecaoQuandoUsuarioNaoEncontrado() {
        when(usuarioRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(UsuarioNaoEncontradoException.class,
            () -> usuarioService.buscarPorId(99L));

        verify(usuarioRepository).findById(99L);
    }

    // ── TDD: atualização de plano ──────────────────────────
    @Test
    void deveAtualizarPlanoComSucesso() {
        Usuario u = new Usuario("Carlos", "carlos@email.com", "hash", Plano.BASIC);
        when(usuarioRepository.findById(1L)).thenReturn(Optional.of(u));
        when(usuarioRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        Usuario resultado = usuarioService.atualizarPlano(1L, Plano.PREMIUM);

        assertEquals(Plano.PREMIUM, resultado.getPlano());
    }
}
