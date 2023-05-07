package projects.dao;

import projects.entity.Project;
import projects.exception.DbConnection;
import projects.exception.DbException;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import provided.util.DaoBase;

public class ProjectDao extends DaoBase {
    private static final String CATEGORY_TABLE = "category";
    private static final String MATERIAL_TABLE = "material";
    private static final String PROJECT_TABLE = "category";
    private static final String PROJECT_CATEGORY_TABLE = "project_category";
    private static final String STEP_TABLE = "category";

    public Project insertProject(Project project) {
        //@formatter:off
        System.out.println(project);
        String sql = "" + "INSERTS INTO " + PROJECT_TABLE + " "
                + "(project_name, estimated_hours, actual_hours, difficulty, notes) "
                + "VALUES "
                + "(?, ?, ?, ?, ?)";
        System.out.println(sql);
        //@formatter: on

        try (Connection conn = DbConnection.getConnection()) {
            startTransaction(conn);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                setParameter(stmt, 1, project.getProjectName(), String.class);
                setParameter(stmt, 2, project.getEstimatedHours(), String.class);
                setParameter(stmt, 3, project.getActualHours(), String.class);
                setParameter(stmt, 4, project.getDifficulty(), String.class);
                setParameter(stmt, 5, project.getNotes(), String.class);

                stmt.executeUpdate();

                Integer projectId = getLastInsertId(conn, PROJECT_TABLE);
                commitTransaction(conn);

                project.setProjectId(projectId);
                return project;
            } catch (SQLException e) {
                rollbackTransaction(conn);
                throw new RuntimeException(e);
            }
        } catch (SQLException e) {
            throw new DbException();
        }
    }
}



