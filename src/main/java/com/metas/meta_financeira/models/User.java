package com.metas.meta_financeira.models;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name  ="app_user")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "oauth_id", unique = true)
    private String oauthId;

    @Column(unique = true)
    private String email;

    private String name;

    @Column(name = "picture")
    public String picture;

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Meta> metas = new ArrayList<>();

    public User() {}

    //Construtor
    public User(String oauthId, String email, String name, String picture) {
        this.oauthId = oauthId;
        this.email = email;
        this.name = name;
        this.picture = picture;
    }

    //Getters e Setters
    public Long getId() {return id;}

    public String getOauthId() { return oauthId; }
    public void setOauthId(String oauthId) { this.oauthId = oauthId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPictureUrl() { return picture; }
    public void setPictureUrl(String pictureUrl) { this.picture = pictureUrl; }

    public List<Meta> getMetas() { return metas; }
    public void setMetas(List<Meta> metas) { this.metas = metas; }

    //Métodos
    public void addMeta(Meta meta) {
        metas.add(meta);
        meta.setOwner(this);
    }

    public void removeMeta(Meta meta) {
        metas.remove(meta);
        meta.setOwner(null);
    }

}
