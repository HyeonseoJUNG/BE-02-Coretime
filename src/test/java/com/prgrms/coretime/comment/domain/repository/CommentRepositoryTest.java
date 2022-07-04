package com.prgrms.coretime.comment.domain.repository;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import com.prgrms.coretime.TestConfig;
import com.prgrms.coretime.comment.domain.Comment;
import com.prgrms.coretime.comment.domain.CommentLike;
import com.prgrms.coretime.comment.dto.response.CommentOneResponse;
import com.prgrms.coretime.post.domain.Board;
import com.prgrms.coretime.post.domain.BoardType;
import com.prgrms.coretime.post.domain.Post;
import com.prgrms.coretime.post.domain.repository.BoardRepository;
import com.prgrms.coretime.post.domain.repository.PostRepository;
import com.prgrms.coretime.school.domain.School;
import com.prgrms.coretime.school.domain.respository.SchoolRepository;
import com.prgrms.coretime.user.domain.LocalUser;
import com.prgrms.coretime.user.domain.OAuthUser;
import com.prgrms.coretime.user.domain.User;
import com.prgrms.coretime.user.domain.repository.UserRepository;
import java.nio.channels.IllegalChannelGroupException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;

@DataJpaTest
@Rollback(false) // query 확인하기 위해서 추후 지우겠습니당.
@ActiveProfiles("test")
@Import(TestConfig.class)
@TestInstance(Lifecycle.PER_CLASS)
class CommentRepositoryTest {

  @PersistenceContext
  EntityManager em;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private PostRepository postRepository;

  @Autowired
  private BoardRepository boardRepository;

  @Autowired
  private CommentRepository commentRepository;

  @Autowired
  private CommentLikeRepository commentLikeRepository;

  @Autowired
  private SchoolRepository schoolRepository;

  private String localTestEmail = "local@university.ac.kr";
  private String oauthTestEmail = "oauth@ajou.ac.kr";

  private User localUser;

  private User oauthUser;

  private Board board;

  private Post anonyPost;

  private Comment parent;

  private School school;

  void setSchool() {
    school = new School("university", "university@university.ac.kr");
    school = schoolRepository.save(school);
  }

  void setUser() {
    localUser = LocalUser.builder()
        .nickname("local유저")
        .profileImage("예시 링크")
        .email(localTestEmail)
        .name("김승은로컬")
        .school(school)
        .password("test1234!")
        .build();

    oauthUser = OAuthUser.builder()
        .nickname("oauth유저")
        .profileImage("예시 링크")
        .email(oauthTestEmail)
        .name("김승은oauth")
        .school(school)
        .provider("카카오")
        .providerId("카카오id")
        .build();

    localUser = userRepository.save(localUser);
    oauthUser = userRepository.save(oauthUser);
  }

  void setBoard() {
    board = Board.builder()
        .category(BoardType.BASIC)
        .name("게시판")
        .school(school)
        .build();
    board = boardRepository.save(board);
  }

  /**
   * 주의: 현재 user와 board가 db에 있는 인스턴스일 수 잇는건 순서를 엄격하게 맞춘 탓임.
   */
  void setPost() {
    anonyPost = Post.builder()
        .title("아 테스트 세팅하는데 손아파 죽겠다")
        .content("ㅈㄱㄴ")
        .isAnonymous(true)
        .user(localUser)
        .board(board)
        .build();
    anonyPost = postRepository.save(anonyPost);
  }

  void setComment() {
    parent = Comment.builder()
        .user(localUser)
        .post(anonyPost)
        .parent(null)
        .isAnonymous(true)
        .anonymousSeq(anonyPost.getAnonymousSeqAndAdd())
        .content("응 근데 테스트 안짜면 니 망해~")
        .build();

    parent = commentRepository.save(parent);
  }

  @BeforeAll
  void setup() {
    setSchool();
    setUser();
    setBoard();
    setPost();
    setComment();
  }

  @Test
  @DisplayName("부모 댓글과 자식 댓글이 양방향으로 연결 되어 있는지")
  public void testParentChild() {
    Comment child = Comment.builder()
        .user(localUser)
        .post(anonyPost)
        .parent(parent)
        .isAnonymous(true)
        .content("나는 자식댓글")
        .build();

    Comment savedChild = commentRepository.save(child);

    em.flush();
    em.clear();

    Comment calledChild = commentRepository.findById(savedChild.getId())
        .orElseThrow(IllegalArgumentException::new);
    Comment calledParent = commentRepository.findById(parent.getId())
        .orElseThrow(IllegalArgumentException::new);

    assertThat(calledChild.getParent()).isEqualTo(calledParent);
  }

  @Test
  @DisplayName("Post의 댓글 제대로 들어가있는지 파악하기")
  public void testCommentOfPost() {
    Comment realComment = Comment.builder()
        .user(localUser)
        .post(anonyPost)
        .parent(null)
        .isAnonymous(false)
        .content("나는 실명댓글")
        .build();

    Comment child = Comment.builder()
        .user(localUser)
        .post(anonyPost)
        .parent(parent)
        .isAnonymous(true)
        .content("나는 자식댓글")
        .build();

    Comment savedRealParent = commentRepository.save(realComment);
    Comment savedChild = commentRepository.save(child);

    em.flush();
    em.clear();

    Post masterPost = postRepository.findById(anonyPost.getId())
        .orElseThrow(IllegalArgumentException::new);

    assertThat(masterPost.getComments().size()).isEqualTo(3);
  }

  @Test
  @DisplayName("update Delete 제대로 저장 되는지")
  public void testDelete() {
    Comment calledParent = commentRepository.findById(parent.getId())
        .orElseThrow(IllegalChannelGroupException::new);
    calledParent.updateDelete();

    em.flush();
    em.clear();

    Comment updatedComment = commentRepository.findById(parent.getId())
        .orElseThrow(IllegalArgumentException::new);

    assertThat(updatedComment.getIsDelete()).isTrue();
  }


  @Test
  @DisplayName("best 댓글이 존재할 때 값을 찾아오는지")
  public void testFindBest() {
    //given
    List<Long> userIds = new ArrayList<>();

    for (int i = 0; i < 10; i++) {
      LocalUser savedUser = userRepository.save(LocalUser.builder()
          .nickname("local유저" + i)
          .profileImage("예시 링크" + i)
          .email(localTestEmail + i)
          .name("김승은로컬")
          .school(school)
          .password("test1234!")
          .build());
      userIds.add(savedUser.getId());
    }

    em.flush();
    em.clear();

    for (int i = 0; i < 10; i++) {
      User user = userRepository.findById(userIds.get(i)).get();
      commentLikeRepository.save(new CommentLike(user, parent));
    }

    commentRepository.save(Comment.builder()
        .user(localUser)
        .post(anonyPost)
        .parent(parent)
        .anonymousSeq(null)
        .isAnonymous(false)
        .content("나는 자식댓글")
        .build());

    em.flush();
    em.clear();

    //when
    Optional<CommentOneResponse> bestComment = commentRepository.findBestCommentByPost(
        anonyPost.getId());

    //then
    assertThat(bestComment).isNotEmpty();
    CommentOneResponse bestCommentResponse = bestComment.get();
    assertThat(bestCommentResponse.getCommentId()).isEqualTo(parent.getId());
  }

  @Test
  @DisplayName("best 댓글이 좋아요가 10개 안될 때 null로 가져오는지")
  public void testBestNull() {
    //given
    List<Long> userIds = new ArrayList<>();

    for (int i = 0; i < 9; i++) {
      LocalUser savedUser = userRepository.save(LocalUser.builder()
          .nickname("local유저" + i)
          .profileImage("예시 링크" + i)
          .email(localTestEmail + i)
          .name("김승은로컬")
          .school(school)
          .password("test1234!")
          .build());
      userIds.add(savedUser.getId());
    }

    em.flush();
    em.clear();

    for (int i = 0; i < 9; i++) {
      User user = userRepository.findById(userIds.get(i)).get();
      commentLikeRepository.save(new CommentLike(user, parent));
    }

    commentRepository.save(Comment.builder()
        .user(localUser)
        .post(anonyPost)
        .parent(parent)
        .isAnonymous(true)
        .content("나는 자식댓글")
        .build());

    em.flush();
    em.clear();

    //when
    Optional<CommentOneResponse> bestComment = commentRepository.findBestCommentByPost(
        anonyPost.getId());

    //then
    assertThat(bestComment).isEmpty();
  }

  @Test
  @DisplayName("한 게시글에 좋아요가 10개 이상인 댓글이 두 개 이상일 때 제일 높은 걸 찾아오는 지")
  public void testCorrectBest() {
    //given
    List<Long> userIds = new ArrayList<>();
    List<Long> extraIds = new ArrayList<>();

    Comment child = Comment.builder()
        .user(localUser)
        .post(anonyPost)
        .parent(parent)
        .anonymousSeq(null)
        .isAnonymous(false)
        .content("나는 자식댓글")
        .build();

    commentRepository.save(child);

    for (int i = 0; i < 10; i++) {
      LocalUser savedUser = userRepository.save(LocalUser.builder()
          .nickname("local유저" + i)
          .profileImage("예시 링크" + i)
          .email(localTestEmail + i)
          .name("김승은로컬")
          .school(school)
          .password("test1234!")
          .build());
      userIds.add(savedUser.getId());
    }

    for (int i = 10; i < 20; i++) {
      LocalUser savedUser = userRepository.save(LocalUser.builder()
          .nickname("local유저" + i)
          .profileImage("예시 링크" + i)
          .email(localTestEmail + i)
          .name("김승은로컬")
          .school(school)
          .password("test1234!")
          .build());
      extraIds.add(savedUser.getId());
    }

    em.flush();
    em.clear();

    for (int i = 0; i < 10; i++) {
      User user = userRepository.findById(userIds.get(i)).get();
      commentLikeRepository.save(new CommentLike(user, parent));
      commentLikeRepository.save(new CommentLike(user, child));
    }

    for (int i = 0; i < 10; i++) {
      User user = userRepository.findById(extraIds.get(i)).get();
      commentLikeRepository.save(new CommentLike(user, child));
    }

    em.flush();
    em.clear();

    //when
    Optional<CommentOneResponse> bestComment = commentRepository.findBestCommentByPost(
        anonyPost.getId());

    //then
    assertThat(bestComment).isNotEmpty();
    CommentOneResponse bestCommentResponse = bestComment.get();
    assertThat(bestCommentResponse.getLike()).isEqualTo(20);
    assertThat(bestCommentResponse.getName()).isEqualTo(child.getUser().getNickname());
    assertThat(bestCommentResponse.getCommentId()).isEqualTo(child.getId());
  }

}