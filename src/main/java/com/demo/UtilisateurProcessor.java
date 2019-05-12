package com.demo;

import com.model.Utilisateur;

import org.springframework.batch.item.ItemProcessor;


public class UtilisateurProcessor implements ItemProcessor<Utilisateur, Utilisateur> {

    @Override
    public Utilisateur process(Utilisateur utilisateur) throws Exception{
        return utilisateur;
    }
}
