package de.terrestris.shogun.lib.model.security.permission;

import de.terrestris.shogun.lib.model.User;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.envers.AuditTable;
import org.hibernate.envers.Audited;

@Entity(name = "userclasspermissions")
@Table(schema = "shogun")
@Audited
@AuditTable(value = "userclasspermissions_rev", schema = "shogun_rev")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class UserClassPermission extends ClassPermission {

    @ManyToOne(optional = false)
    private User user;

}
