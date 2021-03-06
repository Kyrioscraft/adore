package cn.kyrioscraft.data.service;

import cn.kyrioscraft.data.dto.RoleDTO;
import cn.kyrioscraft.data.dto.UserDTO;
import cn.kyrioscraft.data.dto.UserPasswordDTO;
import cn.kyrioscraft.data.enums.ResetPasswordResult;
import cn.kyrioscraft.data.model.*;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface UserService {

    Optional<User> getUserById(long id);

    Optional<User> getByEmail(String email);

    Collection<User> getAllUsers();

    User create(UserDTO form);

    User getUserByUsername(String username);

    @Transactional(readOnly = true)
    Collection<Authority> getRoles();

    List<User> getUsersWithDetail();

	boolean canAccessUser(CurrentUser currentUser, String username);

	UserConnection getUserConnectionByUserId(String userId);

    @Transactional
    ResetPasswordResult updatePassword(UserPasswordDTO userPasswordDTO);

    @Transactional
    UserToken createUserToken(User user);

    @Transactional
    Optional<UserToken> getUserToken(String token);

    User update(UserDTO userDTO);

    @Transactional(readOnly = true)
    Optional<User> getUserByIdWithDetail(Long ID);

    Authority createAuthority(RoleDTO roleDTO);

    Authority updateAuthority(RoleDTO roleDTO);

    Authority getAuthorityById(Long id);

    void deleteAuthority(Authority authority, Collection<User> users);

    Collection<User> getUsersByAuthorityId(Long authorityId);

    User updateHasAvatar(Long userId, boolean hasAvatar);

}
