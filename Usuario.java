import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long          id;
    private String        nome;

    @Column(unique = true)
    private String        email;
    private String        senha;         // armazenada criptografada (BCrypt)

    @Enumerated(EnumType.STRING)
    private Plano         plano;         // BASIC, STANDARD, PREMIUM

    @Enumerated(EnumType.STRING)
    private StatusConta   status;        // ATIVA, SUSPENSA, CANCELADA

    private LocalDateTime criadoEm;
    private LocalDateTime ultimoLogin;

    public Usuario() {}

    public Usuario(String nome, String email, String senha, Plano plano) {
        this.nome      = nome;
        this.email     = email;
        this.senha     = senha;
        this.plano     = plano;
        this.status    = StatusConta.ATIVA;
        this.criadoEm  = LocalDateTime.now();
    }

    public Long          getId()          { return id; }
    public String        getNome()        { return nome; }
    public String        getEmail()       { return email; }
    public String        getSenha()       { return senha; }
    public Plano         getPlano()       { return plano; }
    public StatusConta   getStatus()      { return status; }
    public LocalDateTime getCriadoEm()   { return criadoEm; }
    public LocalDateTime getUltimoLogin() { return ultimoLogin; }

    public void setNome(String nome)               { this.nome        = nome; }
    public void setEmail(String email)             { this.email       = email; }
    public void setSenha(String senha)             { this.senha       = senha; }
    public void setPlano(Plano plano)              { this.plano       = plano; }
    public void setStatus(StatusConta status)      { this.status      = status; }
    public void setUltimoLogin(LocalDateTime dt)   { this.ultimoLogin = dt; }
}
