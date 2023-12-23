package com.github.straightth.mapper.user;

import com.github.straightth.domain.User;
import com.github.straightth.dto.response.UserHimselfResponse;
import com.github.straightth.dto.response.UserResponse;
import com.github.straightth.dto.response.UserShortResponse;
import com.github.straightth.mapper.project.ProjectMapperEnricher;
import java.util.ArrayList;
import java.util.Collection;
import javax.annotation.processing.Generated;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-12-23T18:58:50+0100",
    comments = "version: 1.5.5.Final, compiler: javac, environment: Java 21.0.1 (BellSoft)"
)
@Component
public class UserMapperImpl implements UserMapper {

    @Autowired
    private ProjectMapperEnricher projectMapperEnricher;

    @Override
    public UserShortResponse userToUserShortResponse(User user) {
        if ( user == null ) {
            return null;
        }

        UserShortResponse.UserShortResponseBuilder userShortResponse = UserShortResponse.builder();

        userShortResponse.id( user.getId() );
        userShortResponse.username( user.getUsername() );
        userShortResponse.initials( user.getInitials() );
        userShortResponse.role( user.getRole() );

        return userShortResponse.build();
    }

    @Override
    public Collection<UserShortResponse> usersToUserShortResponses(Collection<User> users) {
        if ( users == null ) {
            return null;
        }

        Collection<UserShortResponse> collection = new ArrayList<UserShortResponse>( users.size() );
        for ( User user : users ) {
            collection.add( userToUserShortResponse( user ) );
        }

        return collection;
    }

    @Override
    public UserResponse userToUserResponse(User user) {
        if ( user == null ) {
            return null;
        }

        UserResponse.UserResponseBuilder userResponse = UserResponse.builder();

        userResponse.mutualProjects( projectMapperEnricher.userToProjectShortResponse( user.getId() ) );
        userResponse.id( user.getId() );
        userResponse.username( user.getUsername() );
        userResponse.initials( user.getInitials() );
        userResponse.email( user.getEmail() );
        userResponse.telegram( user.getTelegram() );
        userResponse.role( user.getRole() );
        userResponse.phoneNumber( user.getPhoneNumber() );

        return userResponse.build();
    }

    @Override
    public UserHimselfResponse userToUserHimselfResponse(User user) {
        if ( user == null ) {
            return null;
        }

        UserHimselfResponse.UserHimselfResponseBuilder userHimselfResponse = UserHimselfResponse.builder();

        userHimselfResponse.id( user.getId() );
        userHimselfResponse.username( user.getUsername() );
        userHimselfResponse.email( user.getEmail() );
        userHimselfResponse.telegram( user.getTelegram() );
        userHimselfResponse.role( user.getRole() );
        userHimselfResponse.phoneNumber( user.getPhoneNumber() );

        return userHimselfResponse.build();
    }
}
