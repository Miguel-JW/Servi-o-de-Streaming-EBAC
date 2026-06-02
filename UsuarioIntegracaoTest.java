import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class UsuarioIntegracaoTest {

    @Autowired MockMvc          mockMvc;
    @Autowired UsuarioRepository usuarioRepository;

    @BeforeEach
    void limpar() { usuarioRepository.deleteAll(); }

    // ── Integração: cadastro retorna 201 ───────────────────
    @Test
    void deveCadastrarUsuarioERetornar201() throws Exception {
        mockMvc.perform(post("/usuarios/cadastrar")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    { "nome":"Miguel","email":"miguel@email.com",
                      "senha":"senha123","plano":"PREMIUM" }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.nome").value("Miguel"))
            .andExpect(jsonPath("$.plano").value("PREMIUM"))
            .andExpect(jsonPath("$.status").value("ATIVA"));
    }

    // ── Integração: email duplicado retorna 409 ────────────
    @Test
    void deveRetornar409QuandoEmailDuplicado() throws Exception {
        String body = """
            { "nome":"Ana","email":"ana@email.com","senha":"123","plano":"BASIC" }
            """;
        mockMvc.perform(post("/usuarios/cadastrar")
                .contentType(MediaType.APPLICATION_JSON).content(body));
        mockMvc.perform(post("/usuarios/cadastrar")
                .contentType(MediaType.APPLICATION_JSON).content(body))
            .andExpect(status().isConflict());
    }

    // ── Integração: login retorna token JWT ────────────────
    @Test
    void deveRealizarLoginERetornarToken() throws Exception {
        mockMvc.perform(post("/usuarios/cadastrar")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"nome":"Carlos","email":"carlos@email.com","senha":"senha123","plano":"STANDARD"}
                    """));

        mockMvc.perform(post("/usuarios/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"carlos@email.com","senha":"senha123"}
                    """))
            .andExpect(status().isOk())
            .andExpect(content().string(org.hamcrest.Matchers.not("")));
    }

    // ── Integração: suspender conta ────────────────────────
    @Test
    void deveSuspenderContaEBloquearLogin() throws Exception {
        mockMvc.perform(post("/usuarios/cadastrar")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"nome":"Bia","email":"bia@email.com","senha":"abc123","plano":"BASIC"}
                    """));

        Long id = usuarioRepository.findByEmail("bia@email.com").get().getId();

        mockMvc.perform(patch("/usuarios/" + id + "/suspender"))
            .andExpect(status().isNoContent());

        mockMvc.perform(post("/usuarios/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email":"bia@email.com","senha":"abc123"}
                    """))
            .andExpect(status().isUnauthorized());
    }
}
