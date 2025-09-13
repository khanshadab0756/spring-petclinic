package org.springframework.samples.petclinic.owner;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/owners/{ownerId}")
public class PetTypeController {

    private final PetTypeRepository types;

    public PetTypeController(PetTypeRepository types){
       this.types = types;
    }

    @GetMapping("/pet-types")
    @ResponseBody
    public ResponseEntity<List<PetType>> getAllPetTypes() {
       List<PetType> petTypes = this.types.findAll();

       return new ResponseEntity<>(petTypes, HttpStatus.OK);
    }

    @GetMapping("/pet-types/{name}")
    @ResponseBody
    public ResponseEntity<PetType> getPetsTypeByName(@PathVariable String name) {
       PetType petType = this.types.findPetTypesByName(name);

       return new ResponseEntity<>(petType, HttpStatus.OK);
    }

    @PostMapping("/pet-type")
    @ResponseBody
    public ResponseEntity<String> addPetType(@RequestBody PetType type) {
       this.types.save(type);

       return new ResponseEntity<>(HttpStatus.CREATED);

    }
}
