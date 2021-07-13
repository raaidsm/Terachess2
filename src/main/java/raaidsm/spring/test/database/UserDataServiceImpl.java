package raaidsm.spring.test.database;

import org.springframework.stereotype.Service;

@Service
public class UserDataServiceImpl {
    private final UserRepo userRepo;
    private final ColourDataServiceImpl colourDataService;

    public UserDataServiceImpl(UserRepo userRepo, ColourDataServiceImpl colourDataService) {
        this.userRepo = userRepo;
        this.colourDataService = colourDataService;
    }

    public String addUser(String username) {
        //OVERVIEW: Returns colour of the newly created user, null if both colours are taken up
        //Initialize user to be created
        UserEntity userToBeCreated = new UserEntity();
        userToBeCreated.setName(username);

        //Check available colour
        String availableColour = colourDataService.getNextAvailableColour();
        if (availableColour == null) return null;
        userToBeCreated.setColour(availableColour);

        //Add user to database and return true
        userRepo.save(userToBeCreated);
        return availableColour;
    }
}