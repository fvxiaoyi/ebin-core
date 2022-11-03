package apps.user.infrastructure.repo;

import apps.user.domain.User;
import apps.user.domain.service.UserRepo;
import core.framework.jpa.mongodb.impl.AbstractMongoDBRepository;
import org.springframework.stereotype.Repository;

/**
 * @author ebin
 */
@Repository
public class UserRepoImpl extends AbstractMongoDBRepository<User> implements UserRepo {

}
