package cleanie.repatch.post.repository;

import cleanie.repatch.post.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PostRepository extends JpaRepository<Post, Long> {
}
