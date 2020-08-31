package de.terrestris.shogun.boot.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ApplicationInfo {

    private String version;

    private String buildTime;

    private Long userId;

    private List<String> authorities;
}
