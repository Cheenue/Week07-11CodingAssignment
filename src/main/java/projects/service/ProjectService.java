package projects.service;

import projects.dao.ProjectDao;
import projects.entity.Project;
import projects.exception.DbException;

import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

public class ProjectService {
    private ProjectDao projectDao = new ProjectDao();

    //This method calls the projectDao class and instantiates it

    public Project addProject (Project project) {
        return projectDao.insertProject(project);
    }

    //This method calls projectDao and retrieves all the projects without the materials, steps and categories and returns a list
    //of project records

    public List<Project> fetchAllProjects() {
        return projectDao.fetchAllProjects();
    }

//    This method calls the project DAO to get all project details, including materials, steps, and categories.
//    If the project ID is invalid, it throws an exception.

    public Project fetchProjectById(Integer projectId) {
        return projectDao.fetchProjectById(projectId).orElseThrow(() -> new NoSuchElementException(
                "Project with project ID=" + projectId + " does not exist."
        ));
    }

    public void modifyProjectDetails(Project project) throws SQLException {
        if (!projectDao.modifyProjectDetails(project)) {
            throw new DbException("Project with ID = " + project.getProjectId() + " does not exist.");
        }
    }

    public void deleteProject(Integer projectId) throws SQLException {
        if(!projectDao.deleteProject(projectId)) {
            throw new DbException("Project with ID = " + projectId + "does not exist.");
        }
    }
}

