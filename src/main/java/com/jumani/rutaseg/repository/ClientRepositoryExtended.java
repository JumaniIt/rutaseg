package com.jumani.rutaseg.repository;

import org.springframework.lang.Nullable;
import com.jumani.rutaseg.domain.Client;

import java.util.List;

public interface ClientRepositoryExtended {

    List<Client> search(@Nullable Long userId,
                        @Nullable String nameLike,
                        @Nullable String phoneLike,
                        @Nullable Long cuit,
                        int limit);
}
