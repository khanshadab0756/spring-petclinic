
            Owner owner = optionalOwner.orElseThrow(() -> new IllegalArgumentException(
                        "Owner not found with id: " + ownerId + ". Please ensure the ID is correct "));
            return owner.getPet(petId);
      }

      @InitBinder("owner")
      public void initOwnerBinder(WebDataBinder dataBinder) {
            dataBinder.setDisallowedFields("id");
      }

      @InitBinder("pet")
      public void initPetBinder(WebDataBinder dataBinder) {
            dataBinder.setValidator(new PetValidator());
      }

      @GetMapping("/pets/new")
      public String initCreationForm(Owner owner, ModelMap model) {
            Pet pet = new Pet();
            owner.addPet(pet);
            return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
      }

      @PostMapping("/pets/new")
      public String processCreationForm(Owner owner, @Valid Pet pet, BindingResult result,
                  RedirectAttributes redirectAttributes) {

            if (StringUtils.hasText(pet.getName()) && pet.isNew() && owner.getPet(pet.getName(), true) != null)
                  result.rejectValue("name", "duplicate", "already exists");

            LocalDate currentDate = LocalDate.now();
            if (pet.getBirthDate() != null && pet.getBirthDate().isAfter(currentDate)) {
                  result.rejectValue("birthDate", "typeMismatch.birthDate");
            }

            if (result.hasErrors()) {
                  return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
            }

            owner.addPet(pet);
            this.owners.save(owner);
            redirectAttributes.addFlashAttribute("message", "New Pet has been Added");
            return "redirect:/owners/{ownerId}";
      }

      @GetMapping("/pets/{petId}/edit")
      public String initUpdateForm() {
            return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
      }

      @GetMapping("/pets")
      @ResponseBody
      public ResponseEntity<List<Pet>> pets() {
            return new ResponseEntity<>(this.petRepository.findAll(), HttpStatus.OK);
      }

      @GetMapping("/pets-type")
      @ResponseBody
      public ResponseEntity<List<PetType>> petsTypes() {
            List<PetType> petTypes = this.types.findAll();

            return new ResponseEntity<>(petTypes,HttpStatus.OK);
      }

      @GetMapping("/pets-type/{name}")
      @ResponseBody
      public ResponseEntity<PetType> petsTypes(@PathVariable String name) {
            PetType petType = this.types.findPetTypesByName(name);

            return new ResponseEntity<>(petType,HttpStatus.OK);
      }

      @GetMapping("/pet/{name}")
      @ResponseBody
      public Pet pets(@PathVariable String name) {
            return this.petRepository.findByName(name);
      }

      @PostMapping("/pet")
	  @ResponseBody
      public ResponseEntity<String> pet(@RequestBody PetType type){
            this.types.save(type);

            return new ResponseEntity<>(HttpStatus.CREATED);

      }

      @PostMapping("/pets/{petId}/edit")
      public String processUpdateForm(Owner owner, @Valid Pet pet, BindingResult result,
                  RedirectAttributes redirectAttributes) {

            String petName = pet.getName();

            // checking if the pet name already exists for the owner
            if (StringUtils.hasText(petName)) {
                  Pet existingPet = owner.getPet(petName, false);
                  if (existingPet != null && !existingPet.getId().equals(pet.getId())) {
                        result.rejectValue("name", "duplicate", "already exists");
                  }
            }

            LocalDate currentDate = LocalDate.now();
            if (pet.getBirthDate() != null && pet.getBirthDate().isAfter(currentDate)) {
                  result.rejectValue("birthDate", "typeMismatch.birthDate");
            }

            if (result.hasErrors()) {
                  return VIEWS_PETS_CREATE_OR_UPDATE_FORM;
            }

            updatePetDetails(owner, pet);
            redirectAttributes.addFlashAttribute("message", "Pet details has been edited");
            return "redirect:/owners/{ownerId}";
      }

      /**
       * Updates the pet details if it exists or adds a new pet to the owner.
       * @param owner The owner of the pet
       * @param pet The pet with updated details
       */
      private void updatePetDetails(Owner owner, Pet pet) {
            Pet existingPet = owner.getPet(pet.getId());
            if (existingPet != null) {
                  // Update existing pet's properties
                  existingPet.setName(pet.getName());
                  existingPet.setBirthDate(pet.getBirthDate());
                  existingPet.setType(pet.getType());
                  existingPet.setLength(pet.getLength());
                  existingPet.setWeight(pet.getWeight());
            }
            else {
                  owner.addPet(pet);
            }
            this.owners.save(owner);
      }

}
