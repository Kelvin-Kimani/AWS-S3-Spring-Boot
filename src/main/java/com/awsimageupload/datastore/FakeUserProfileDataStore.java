package com.awsimageupload.datastore;

import com.awsimageupload.profile.UserProfile;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
public class FakeUserProfileDataStore {

    private final static List<UserProfile> USER_PROFILES = new ArrayList<>();

    static {
        USER_PROFILES.add(new UserProfile(UUID.fromString("b055d346-5bd3-400c-92ac-e4bb57f36a46"), "GeoffreyNdung'u", null));
        USER_PROFILES.add(new UserProfile(UUID.fromString("46dbbdc7-9946-4eab-a047-7671afb04bf4"), "KelvinKimani", null));
        USER_PROFILES.add(new UserProfile(UUID.fromString("68aa1d02-48d9-4394-a91e-708cd0b22f96"), "FaithWanjiru", null));
    }

    public List<UserProfile> getUserProfiles(){
        return USER_PROFILES;
    }
}
