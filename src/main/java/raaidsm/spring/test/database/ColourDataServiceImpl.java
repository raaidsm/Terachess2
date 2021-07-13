package raaidsm.spring.test.database;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ColourDataServiceImpl {
    private final ColourRepo colourRepo;

    public ColourDataServiceImpl(ColourRepo colourRepo) {
        this.colourRepo = colourRepo;
    }

    public String getNextAvailableColour() {
        //Declare variables
        List<ColourEntity> availableColours = colourRepo.findAll();
        ColourEntity toRemove = null;

        //Determine colour to return
        for (ColourEntity availableColour : availableColours) {
            if (availableColour.getColourName().equals("white")) {
                toRemove = availableColour;
                break;
            }
            else if (availableColour.getColourName().equals("black")) {
                toRemove = availableColour;
                break;
            }
        }

        //Remove the colour from the available colours and return it (unless there is no available colour of course)
        if (toRemove == null) return null;
        colourRepo.delete(toRemove);
        return toRemove.getColourName();
    }
}