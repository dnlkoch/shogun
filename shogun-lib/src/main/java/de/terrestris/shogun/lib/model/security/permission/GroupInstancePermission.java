package de.terrestris.shogun.lib.model.security.permission;

import de.terrestris.shogun.lib.model.Group;
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

@Entity(name = "groupinstancepermissions")
@Table(schema = "shogun")
@Audited
@AuditTable(value = "groupinstancepermissions_rev", schema = "shogun_rev")
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class GroupInstancePermission extends InstancePermission {

    @ManyToOne(optional = false)
    private Group group;

}
