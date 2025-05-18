package com.gcu.controller.util;

import com.gcu.model.db.UserModel;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * utilities that apply to UserEntity or UserModel objects, and the views that may render them
 */
public class UserUtil {

    /**
     * build a hex string from an array of bytes
     * @param array of bytes
     * @return a hex string
     */
    public String hex(byte[] array) {
        StringBuilder sb = new StringBuilder();

        for (byte b : array) {
            sb.append(Integer.toHexString((b & 0xFF) | 0x100), 1, 3);
        }

        return sb.toString();
    }

    /**
     * convert a string into a sha256hex
     * @param msg the string to convert
     * @return the resulting hex
     */
    public String sha256Hex(String msg) {
        String message = msg.toLowerCase();
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return hex(md.digest(message.getBytes("UTF-8")));
        } catch (NoSuchAlgorithmException | UnsupportedEncodingException e) {
            Logger.getLogger("ERROR").log(Level.SEVERE, e.getMessage());
        }
        return null;
    }

    /**
     * get the gravatar profile picture url for a specific user
     * @param user the user
     * @return the gravatar profile picture url
     */
    public String gravatarURL(UserModel user) {
        if (user.getGravatarEmail() != null) {
            return "https://gravatar.com/avatar/" + sha256Hex(user.getGravatarEmail()) + "?d=mp";
        } else {
            return "https://gravatar.com/avatar/" + sha256Hex(user.getEmail()) + "?d=mp";
        }
    }

}
