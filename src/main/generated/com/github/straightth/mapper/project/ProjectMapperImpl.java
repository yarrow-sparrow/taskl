package com.github.straightth.mapper.project;

import com.github.straightth.domain.Project;
import com.github.straightth.dto.request.CreateProjectRequest;
import com.github.straightth.dto.response.ProjectResponse;
import com.github.straightth.dto.response.ProjectShortResponse;
import com.github.straightth.mapper.user.UserMapperEnricher;
import java.util.ArrayList;
import java.util.Collection;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-12-23T18:58:49+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.1 (BellSoft)"
)
@Component
public class ProjectMapperImpl implements ProjectMapper {

    @Autowired
    private UserMapperEnricher userMapperEnricher;

    @Override
    public Collection<ProjectShortResponse> projectsToProjectShortResponses(Collection<Project> projects) {
        if ( projects == null ) {
            return null;
        }

        Collection<ProjectShortResponse> collection = new ArrayList<ProjectShortResponse>( projects.size() );
        for ( Project project : projects ) {
            collection.add( projectToProjectShortResponse( project ) );
        }

        return collection;
    }

    @Override
    public ProjectResponse projectToProjectResponse(Project project) {
        if ( project == null ) {
            return null;
        }

        ProjectResponse.ProjectResponseBuilder projectResponse = ProjectResponse.builder();

        projectResponse.memberUsers( userMapperEnricher.usersToUserShortResponses( project.getMemberUserIds() ) );
        projectResponse.id( project.getId() );
        projectResponse.name( project.getName() );
        projectResponse.description( project.getDescription() );

        return projectResponse.build();
    }

    @Override
    public Project createProjectRequestToProject(CreateProjectRequest request) {
        if ( request == null ) {
            return null;
        }

        Project.ProjectBuilder project = Project.builder();

        project.name( request.getName() );
        project.description( request.getDescription() );
        Collection<String> collection = request.getMemberUserIds();
        if ( collection != null ) {
            project.memberUserIds( new ArrayList<String>( collection ) );
        }

        return project.build();
    }

    protected ProjectShortResponse projectToProjectShortResponse(Project project) {
        if ( project == null ) {
            return null;
        }

        ProjectShortResponse.ProjectShortResponseBuilder projectShortResponse = ProjectShortResponse.builder();

        projectShortResponse.id( project.getId() );
        projectShortResponse.name( project.getName() );
        projectShortResponse.description( project.getDescription() );

        return projectShortResponse.build();
    }
}
