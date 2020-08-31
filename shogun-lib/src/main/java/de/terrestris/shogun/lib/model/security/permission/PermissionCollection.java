package de.terrestris.shogun.lib.model.security.permission;

import de.terrestris.shogun.lib.enumeration.PermissionCollectionType;
import de.terrestris.shogun.lib.enumeration.PermissionType;
import de.terrestris.shogun.lib.model.BaseEntity;
import java.util.HashSet;
import java.util.Set;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

@Entity(name = "permissions")
@Table(schema = "shogun")
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class PermissionCollection extends BaseEntity {

    @ElementCollection
    @CollectionTable(name = "permission", schema = "shogun")
    @Enumerated(EnumType.STRING)
    @Fetch(FetchMode.JOIN)
    private Set<PermissionType> permissions = new HashSet<>();

    @Column(unique = true, nullable = false)
    @Enumerated(EnumType.STRING)
    private PermissionCollectionType name;

}
