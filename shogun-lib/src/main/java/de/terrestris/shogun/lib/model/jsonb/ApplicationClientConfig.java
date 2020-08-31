package de.terrestris.shogun.lib.model.jsonb;

import java.io.Serializable;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationClientConfig implements Serializable {
    private String logoPath;
    private Map<String, Object> mapView;
}
