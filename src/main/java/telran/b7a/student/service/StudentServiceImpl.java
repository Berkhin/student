package telran.b7a.student.service;

import java.rmi.StubNotFoundException;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import telran.b7a.student.dao.StudentsMongoRepository;
import telran.b7a.student.dto.ScoreDto;
import telran.b7a.student.dto.StudentCredentialsDto;
import telran.b7a.student.dto.StudentDto;
import telran.b7a.student.dto.UpdateStudentDto;
import telran.b7a.student.dto.exception.StudentNotFoundException;
import telran.b7a.student.model.Student;

@Service
public class StudentServiceImpl implements StudentService {

//	@Autowired
	StudentsMongoRepository studentRepository;
	
//	@Autowired
	ModelMapper modelMapper;
	
	
	@Autowired
	public StudentServiceImpl(StudentsMongoRepository studentRepository, ModelMapper modelMapper) {
		this.studentRepository = studentRepository;
		this.modelMapper = modelMapper;
	}

	@Override
	public boolean addStudent(StudentCredentialsDto studentCredentialsDto) {
		if (studentRepository.findById(studentCredentialsDto.getId()).isPresent()) {
			return false;
		}
//		Student student = new Student(studentCredentialsDto.getId(), studentCredentialsDto.getName(),
//				studentCredentialsDto.getPassword());
		Student student = modelMapper.map(studentCredentialsDto, Student.class);
		studentRepository.save(student);
		return true;
	}

	@Override
	public StudentDto findStudent(Integer id) {
		Student student = studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
		return modelMapper.map(student, StudentDto.class);
//		return StudentDto.builder().id(student.getId()).name(student.getName()).scores(student.getScores()).build();
	}

	@Override
	public StudentDto deleteStudent(Integer id) {
		StudentDto student = findStudent(id);
		studentRepository.deleteById(id);
		return modelMapper.map(student, StudentDto.class);
	}

	@Override
	public StudentCredentialsDto updateStudent(Integer id, UpdateStudentDto updateStudentDto) {
		Student student = studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
		student.setName(updateStudentDto.getName());
		student.setPassword(updateStudentDto.getPassword());
		return modelMapper.map(student, StudentCredentialsDto.class);
	}

	@Override
	public boolean addScore(Integer id, ScoreDto scoreDto) {
		Student student = studentRepository.findById(id).orElseThrow(() -> new StudentNotFoundException(id));
		boolean res = student.addScore(scoreDto.getExamName(), scoreDto.getScore());
		studentRepository.save(student);
		return res;
	}

	@Override
	public List<StudentDto> findStudentsByName(String name) {
		return studentRepository.findAll().stream()
				.filter(s -> name.equalsIgnoreCase(s.getName()))
				.map(s -> modelMapper.map(s, StudentDto.class))
				.collect(Collectors.toList());
	}

}
