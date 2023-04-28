package contacts.commun;

import contacts.data.Categorie;
import contacts.data.Compte;
import contacts.data.Memo;
import contacts.data.Personne;
import contacts.data.Service;
import contacts.data.Telephone;
import javafx.collections.ObservableList;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2023-04-29T00:12:38+0200",
    comments = "version: 1.5.3.Final, compiler: Eclipse JDT (IDE) 1.4.200.v20220802-0458, environment: Java 17.0.4.1 (Eclipse Adoptium)"
)
public class IMapperImpl implements IMapper {

    @Override
    public Compte update(Compte target, Compte source) {
        if ( source == null ) {
            return target;
        }

        target.setId( source.getId() );
        target.setPseudo( source.getPseudo() );
        target.setMotDePasse( source.getMotDePasse() );
        target.setEmail( source.getEmail() );
        if ( target.getRoles() != null ) {
            target.getRoles().clear();
            ObservableList<String> observableList = source.getRoles();
            if ( observableList != null ) {
                target.getRoles().addAll( observableList );
            }
        }

        return target;
    }

    @Override
    public Categorie update(Categorie target, Categorie source) {
        if ( source == null ) {
            return target;
        }

        target.setId( source.getId() );
        target.setLibelle( source.getLibelle() );
        target.setDebut( source.getDebut() );

        return target;
    }

    @Override
    public Memo update(Memo target, Memo source) {
        if ( source == null ) {
            return target;
        }

        target.setId( source.getId() );
        target.setTitre( source.getTitre() );
        target.setDescription( source.getDescription() );

        target.setCategorie( source.getCategorie() );

        return target;
    }

    @Override
    public Personne update(Personne target, Personne source) {
        if ( source == null ) {
            return target;
        }

        target.setId( source.getId() );
        target.setNom( source.getNom() );
        target.setPrenom( source.getPrenom() );
        if ( target.getTelephones() != null ) {
            target.getTelephones().clear();
            ObservableList<Telephone> observableList = source.getTelephones();
            if ( observableList != null ) {
                target.getTelephones().addAll( observableList );
            }
        }

        target.setCategorie( source.getCategorie() );

        return target;
    }

    @Override
    public Service update(Service target, Memo source) {
        if ( source == null ) {
            return target;
        }

        target.setId( source.getId() );

        return target;
    }
}
