package projects.dao;

import projects.entity.Category;
import projects.entity.Material;
import projects.entity.Project;
import projects.entity.Step;
import projects.exception.DbConnection;
import projects.exception.DbException;
import provided.util.DaoBase;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class ProjectDao extends DaoBase {
    private static final String CATEGORY_TABLE = "category";
    private static final String MATERIAL_TABLE = "material";
    private static final String PROJECT_TABLE = "project";
    private static final String PROJECT_CATEGORY_TABLE = "project_category";
    private static final String STEP_TABLE = "step";

    public Project insertProject(Project project) {
        //@formatter:off

        String sql = ""
                + "INSERT INTO " + PROJECT_TABLE + " "
                + "(project_name, estimated_hours, actual_hours, difficulty, notes) "
                + "VALUES "
                + "(?, ?, ?, ?, ?)";
        //@formatter: on

        try (Connection conn = DbConnection.getConnection()) {
            startTransaction(conn);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                setParameter(stmt, 1, project.getProjectName(), String.class);
                setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
                setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
                setParameter(stmt, 4, project.getDifficulty(), Integer.class);
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
            throw new DbException(e);
        }
    }

    public List<Project> fetchAllProjects() {
        String sql = "SELECT * FROM " + PROJECT_TABLE + " ORDER BY project_name";

        try (Connection conn = DbConnection.getConnection()) { //this was red underline because it needed a catch clause
            startTransaction(conn); //this was red for the same reason as above

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                try (ResultSet rs = stmt.executeQuery()) {
                    List<Project> projects = new LinkedList<>();

                    while (rs.next()) {
                        Project projectTemp = new Project();

                        projectTemp.setActualHours(rs.getBigDecimal("actual_hours"));
                        projectTemp.setDifficulty(rs.getObject("difficulty", Integer.class));
                        projectTemp.setEstimatedHours(rs.getBigDecimal("estimated_hours"));
                        projectTemp.setNotes(rs.getString("notes"));
                        projectTemp.setProjectId(rs.getObject("project_id", Integer.class));
                        projectTemp.setProjectName(rs.getString("project_name"));

                        projects.add(projectTemp);
                    }
                    return projects;
                }
            } catch (Exception e) {
                rollbackTransaction(conn); //if it catches an exception, it will rollback all the changes
                throw new DbException(e);
            }
        } catch (SQLException e) {
            throw new DbException(e);
        }
    }



    public Optional<Project> fetchProjectById(Integer projectId) {
        String sql = "SELECT * FROM " + PROJECT_TABLE + " WHERE project_id = ?";

        try (Connection conn = DbConnection.getConnection()) { //this was red underline because it needed a catch clause
            startTransaction(conn); //this was red for the same reason as above

            try {
                Project project2 = null;

                try (PreparedStatement stmt = conn.prepareStatement(sql)) { //prepared statement is a sql query is passed as a parameter
                    setParameter(stmt, 1, projectId, Integer.class);

                    try (ResultSet rs = stmt.executeQuery()) {
                        if (rs.next()) {
                            project2 = new Project();
                            project2.setProjectId(rs.getObject("project_id", Integer.class));
                            project2.setProjectName(rs.getString("project_name"));

                            project2.setActualHours(rs.getBigDecimal("actual_hours"));
                            project2.setDifficulty(rs.getObject("difficulty", Integer.class));
                            project2.setEstimatedHours(rs.getBigDecimal("estimated_hours"));
                            project2.setNotes(rs.getString("notes"));
                            project2.setProjectId(rs.getObject("project_id", Integer.class));
                            project2.setProjectName(rs.getString("project_name"));
                        }
                    }
                }

                if (Objects.nonNull(project2)) {
                    project2.getMaterials().addAll(fetchMaterialsForProject(conn, projectId));
                    project2.getSteps().addAll(fetchStepsForProject(conn, projectId));
                    project2.getCategories().addAll(fetchCategoriesForProject(conn, projectId));
                }

                commitTransaction(conn);

                return Optional.ofNullable(project2);
            } catch (Exception e) {
                rollbackTransaction(conn);
                throw new DbException(e);
            }
        } catch (SQLException e) {
            throw new DbException(e);
        }
    }

    private List<Category> fetchCategoriesForProject(Connection conn, Integer projectId) throws SQLException {
        //formatter:off
        String sql = ""
                + "SELECT c.* FROM " + CATEGORY_TABLE + " c "
                + "JOIN " + PROJECT_CATEGORY_TABLE + " pc USING (category_id) "
                + "WHERE project_id = ?";
        //formatter:on

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            setParameter(stmt, 1, projectId, Integer.class);

            try (ResultSet rs = stmt.executeQuery()) {
                List<Category> categories = new LinkedList<>();

                while (rs.next()) {
                    categories.add(extract(rs, Category.class));
                }
                return categories;
            }
        }
    }

    private List<Step> fetchStepsForProject(Connection conn, Integer projectId) throws SQLException {
        String sql = "SELECT * FROM " + STEP_TABLE + " WHERE project_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            setParameter(stmt, 1, projectId, Integer.class);

            try (ResultSet rs = stmt.executeQuery()) {
                List<Step> steps = new LinkedList<>();

                while (rs.next()) {
                    steps.add(extract(rs, Step.class));
                }
                return steps;
            }
        }
    }


    private List<Material> fetchMaterialsForProject(Connection conn, Integer projectId) throws SQLException {
        String sql = "SELECT * FROM " + MATERIAL_TABLE + " WHERE project_id = ?";

        try (PreparedStatement stmt = conn.prepareStatement(sql)) {
            setParameter(stmt, 1, projectId, Integer.class);

            try (ResultSet rs = stmt.executeQuery()) {
                List<Material> materials = new LinkedList<>();

                while (rs.next()) {
                    materials.add(extract(rs, Material.class));
                }
                return materials;
            }
        }
    }

    public boolean modifyProjectDetails(Project project) {
        //@formatter:off
        String sql = ""
                + "UPDATE " + PROJECT_TABLE + " SET "
                + "project_name = ?, "
                + "estimated_hours = ?, "
                + "actual_hours = ?. "
                + "difficulty = ?, "
                + "notes = ? "
                + "WHERE project_id = ?";
        //@formatter:on

        try (Connection conn = DbConnection.getConnection()) {
            startTransaction(conn);

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                setParameter(stmt, 1, project.getProjectName(), String.class);
                setParameter(stmt, 2, project.getEstimatedHours(), BigDecimal.class);
                setParameter(stmt, 3, project.getActualHours(), BigDecimal.class);
                setParameter(stmt, 4, project.getDifficulty(), Integer.class);
                setParameter(stmt, 5, project.getNotes(), String.class);
                setParameter(stmt, 6, project.getProjectId(), Integer.class);

                boolean modified = stmt.executeUpdate() == 1;
                commitTransaction(conn);

                return modified;
            } catch (Exception e) {
                rollbackTransaction(conn);
                throw new DbException(e);
            }
        } catch (SQLException e) {
            throw new DbException(e);
        }
    }

    public boolean deleteProject(Integer projectId) throws SQLException {
        String sql = "DELETE FROM " + PROJECT_TABLE + " WHERE project_id = ?";

        try (Connection conn = DbConnection.getConnection()) {
            startTransaction(conn);
            //this is trying to start a connection

            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                //prepared statement is sending the SQL statements to the database
                setParameter(stmt, 1, projectId, Integer.class);

                boolean deleted = stmt.executeUpdate() == 1;

                commitTransaction(conn);
                return deleted;
            } catch (Exception e) {
                rollbackTransaction(conn);
                throw new DbException(e);
            }
        }
    }
}





