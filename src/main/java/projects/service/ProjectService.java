package projects.service;

import projects.dao.ProjectDao;
import projects.entity.Project;

import java.util.List;

public class ProjectService {
    private ProjectDao projectDao = new ProjectDao();

    public Project addProject (Project project) {
        return project;
//        return projectDao.insertProject(project);
    }

    public List<Project> fetchAllProjects() {
        return projectDao.fetchAllProjects();
    }
}

