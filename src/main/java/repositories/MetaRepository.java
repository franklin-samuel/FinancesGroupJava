package repositories;

import com.metas.meta_financeira.models.Meta;
import com.metas.meta_financeira.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface MetaRepository extends JpaRepository<Meta, Long> {
    List<Meta> findByOwner(User owner);
}
