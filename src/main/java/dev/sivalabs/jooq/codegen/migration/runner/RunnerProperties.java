package dev.sivalabs.jooq.codegen.migration.runner;

import dev.sivalabs.jooq.codegen.datasource.TargetDatasource;
import java.net.URLClassLoader;
import lombok.Builder;
import lombok.Data;
import lombok.experimental.Accessors;
import lombok.experimental.Delegate;
import org.apache.maven.plugin.logging.Log;
import org.apache.maven.project.MavenProject;

/**
 * Properties for running migration and generation sources
 */
@Data
@Builder
@Accessors(fluent = true)
public final class RunnerProperties {
    /**
     * Maven logger
     */
    private final Log log;
    /**
     * Target project
     */
    private final MavenProject mavenProject;
    /**
     * Maven classloader
     */
    private final URLClassLoader mavenClassloader;

    /**
     * Datasource to migrate and generate sources on
     */
    @Delegate
    private final TargetDatasource targetDatasource;
}
