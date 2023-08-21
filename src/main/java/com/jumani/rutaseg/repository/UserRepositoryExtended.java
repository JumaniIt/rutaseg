package com.jumani.rutaseg.repository;
import com.jumani.rutaseg.domain.User;

import java.util.List;

public interface UserRepositoryExtended {
    List<User> search(Boolean admin, String nickname, String email, int pageSize);
    long count(Boolean admin, String nickname, String email);
}
