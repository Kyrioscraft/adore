package cn.kyrioscraft.data.utils;

import cn.kyrioscraft.data.dto.UserDTO;
import cn.kyrioscraft.data.model.User;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class UserUtils {

    public static UserDTO userToUserDTO(User user) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(user.getId());
        userDTO.setFirstName(user.getFirstName());
        userDTO.setLastName(user.getLastName());
        userDTO.setUsername(user.getUsername());
        userDTO.setPassword(user.getPassword());
        userDTO.setEmail(user.getEmail());
        userDTO.setSignInProvider(user.getSignInProvider());
        userDTO.setAuthorities(user.getAuthorities());
        userDTO.setHasAvatar(user.hasAvatar());
        userDTO.setUserKey(user.getUserKey());
        return userDTO;
    }

    public static String bcryptedPassword(String rawPassword) {
        return new BCryptPasswordEncoder().encode(rawPassword);
    }
}
