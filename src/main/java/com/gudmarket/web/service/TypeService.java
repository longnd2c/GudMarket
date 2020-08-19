package com.gudmarket.web.service;

import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.gudmarket.web.entity.Type;
import com.gudmarket.web.repository.TypeRepository;


@Service
public class TypeService {
	private TypeRepository typeRepo;
	@Autowired
	public TypeService(TypeRepository typeRepo) {
		this.typeRepo=typeRepo;
	}
	
	public void saveType(Type type, String type_name) {
		String id="T";
		do {
			Random rnd = new Random();
			int n = 100000 + rnd.nextInt(900000);
			id=id+n;
		}
		while(typeRepo.findById(id)==null);
		type.setId_type(id);
		type.setType_name(type_name);
		typeRepo.save(type);
	}
	
}
