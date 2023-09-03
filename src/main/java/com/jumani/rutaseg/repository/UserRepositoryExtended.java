package com.jumani.rutaseg.repository;
import com.jumani.rutaseg.domain.User;

import java.util.List;

public interface UserRepositoryExtended {
    List<User> search(
            Boolean admin,
            String nicknameLike,
            String emailLike);

    long count(Boolean admin,
               String nicknameLike,
               String emailLike);
}
