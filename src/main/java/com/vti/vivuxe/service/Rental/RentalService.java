package com.vti.vivuxe.service.Rental;

import com.vti.vivuxe.dto.request.RentalCreationRequest;
import com.vti.vivuxe.dto.response.CarResponse;
import com.vti.vivuxe.dto.response.RentalDTO;
import com.vti.vivuxe.dto.response.UserResponse;
import com.vti.vivuxe.entity.Car;
import com.vti.vivuxe.entity.Rental;
import com.vti.vivuxe.entity.User;
import com.vti.vivuxe.repository.CarRepository;
import com.vti.vivuxe.repository.RentalRepository;
import com.vti.vivuxe.repository.UserRepository;
import com.vti.vivuxe.service.Rental.IRentalService;
import lombok.NoArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.function.Function;

@Service
@NoArgsConstructor
public class RentalService implements IRentalService {
	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private RentalRepository rentalRepository;

	@Autowired
	private CarRepository carRepository;

	@Autowired
	private UserRepository userRepository;



	public void createRental(RentalCreationRequest request) {
		// Validate that request IDs from form are not null
		if (request.getUserId() == null) {
			throw new IllegalArgumentException("User ID must not be null");
		}

		if (request.getCarId() == null) {
			throw new IllegalArgumentException("Car ID must not be null");
		}

		Rental rental = request.asRental();

//		validate that objects from database are not found
		User user = userRepository.findById(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found"));

		Car car = carRepository.findById(request.getCarId())
				.orElseThrow(() -> new RuntimeException("Car not found"));

		rental.setUser(user);
		rental.setCar(car);

		rentalRepository.save(rental);
	}

	public Page<RentalDTO> getAllRentals(Pageable pageable) {
		Page<Rental> rentalList = rentalRepository.findAll(pageable);

		Page<RentalDTO> rentalDTOS = rentalList.map(new Function<Rental, RentalDTO>() {
			@Override
			public RentalDTO apply(Rental entity) {
				RentalDTO dto = new RentalDTO(entity);
				// Conversion logic

				return dto;
			}
		});

		return rentalDTOS;
	}

	public RentalDTO getRentalById(Long id) {
		Optional<Rental> optionalRental = rentalRepository.findById(id);

		if(optionalRental.isEmpty())
		{
			throw new RuntimeException("Rental not found with id: " + id);
		}

		Rental existingRental = optionalRental.get();

		RentalDTO rentalDTO = modelMapper.map(existingRental, RentalDTO.class);

		// Map User to UserResponse
		User user = existingRental.getUser();
		if (user != null) {
			UserResponse userResponse = new UserResponse();
			userResponse.setUserId(user.getUserId());
			userResponse.setUsername(user.getUsername());
			userResponse.setAddress(user.getAddress());
			userResponse.setCreateDate(user.getCreateDate());
			userResponse.setDob(user.getDob());
			userResponse.setDriverLicense(user.getDriverLicense());
			userResponse.setCreateDate(user.getCreateDate());
			userResponse.setEmail(user.getEmail());
			userResponse.setGender(user.getGender().name());
			userResponse.setPhone(user.getPhone());
			userResponse.setRole(user.getRole().name());
			// save User to rentalDTO
			rentalDTO.setUserResponse(userResponse);
		}

		// Map Car to CarResponse
		Car car = existingRental.getCar();
		if (car != null) {
			CarResponse carResponse = new CarResponse();
			carResponse.setCarId(car.getCarId());
			carResponse.setAddress(car.getName());
			carResponse.setName(car.getName());
			carResponse.setBluetooth(car.getBluetooth());
			carResponse.setCamera360(car.getCamera360());
			carResponse.setAirbags(car.getAirbags());
			carResponse.setLicensePlate(car.getLicensePlate());
			carResponse.setCost(car.getCost());
			carResponse.setChildSeat(car.getChildSeat());
			carResponse.setRearCamera(car.getRearCamera());
			carResponse.setCamera360(car.getCamera360());
			carResponse.setDashCamera(car.getDashCamera());
			carResponse.setDvdScreen(car.getDvdScreen());
			carResponse.setEtc(car.getEtc());
			carResponse.setFuel(car.getFuel().name());
			carResponse.setSpareTire(car.getSpareTire());
			carResponse.setGps(car.getGps());
			carResponse.setYear(car.getYear());
			carResponse.setModel(car.getModel());
			carResponse.setMake(car.getMake().name());
			carResponse.setTransmission(car.getTransmission().name());
			carResponse.setSideCamera(car.getSideCamera());
			carResponse.setUsb(car.getUsb());
			carResponse.setSeat(car.getSeat());
			carResponse.setSpareTire(car.getSpareTire());
			carResponse.setStatus(car.getStatus().name());
			carResponse.setDescription(car.getDescription());

			// Map other fields if needed
			rentalDTO.setCarResponse(carResponse);
		}

		return rentalDTO;
	}


	public void updateRental(Long id, RentalCreationRequest request) {
		if(request.getUserId() == null){
			throw new IllegalArgumentException("UserId must not be null");
		}

		if(request.getCarId() == null){
			throw new IllegalArgumentException("CarId must not be null");
		}

		Optional<Rental> optionalRental = rentalRepository.findById(id);


		if (optionalRental.isEmpty()) {
			throw new RuntimeException("Rental not found with id: " + id);
		}

		Rental updatedRental = optionalRental.get();

		User user = userRepository.findById(request.getUserId())
				.orElseThrow(() -> new RuntimeException("User not found"));

		Car car = carRepository.findById(request.getCarId())
				.orElseThrow(() -> new RuntimeException("Car not found"));

		if(request.getUserId() == updatedRental.getUser().getUserId() || request.getCarId() == updatedRental.getCar().getCarId() ){
			throw new IllegalArgumentException("User or Car already existed!");
		}

		updatedRental.setUser(user);

		updatedRental.setCar(car);

		rentalRepository.save(updatedRental);
	}


	public void deleteRentalById(Long id) {
		rentalRepository.findById(id)
				.orElseThrow(() -> new NoSuchElementException("Rental not found with id: " + id));

		Rental rental = rentalRepository.findById(id).get();
		rentalRepository.delete(rental);
	}


}
