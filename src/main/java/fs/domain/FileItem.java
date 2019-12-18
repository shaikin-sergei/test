package fs.domain;

import lombok.Data;

import javax.persistence.*;
import javax.validation.constraints.NotNull;

@Data
@Entity
@Table
public class FileItem {
    @Id
    @GeneratedValue
    private Long id;

    @Column
    @NotNull
    private String name;

    @Column
    private String fsPath;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User owner;

    @Version
    private Long version;

    public FileItem() {
        super();
    }

    public FileItem(Long id) {
        super();
        this.id = id;
    }

    @Override
    public String toString() {
        return String.format("FileItem [id=%d, name=%s, fsPath=%s, owner=%s, version=%d]",
                id, name, fsPath, owner, version);
    }
}
