# Credit :: Outils de calculs pour prêts immobiliers

Modèle Json
-----------

```
{
  "prets": [  // Liste des prets modélisés
    {
      "nom": "PRET1", // Nom du prêt
      "nominal": 2500, // Montant à financer
      "debut": "2011-01-01", // Echéance #0
      "echeances": [ // Liste de série d'échéances. 
        {"debut": 1, "fin": 24, "montant": 10, "taux": 0.00 }
      ],
      "assurances": [
        {"debut": 1, "fin": 24, "capital": 2500, "taux": 0.003 }
      ]
    },
    {
      "nom": "PRET2",
      "nominal": 30000,
      "debut": "2011-01-01",
      "echeances": [
        { "debut": 1, "fin": 120, "montant": 297.18, "taux": 0.03}
      ],
      "assurances": [
        { "debut": 1, "fin": 120, "capital": 30000, "taux": 0.003 }
      ],
      "strategies":[
     	 { "nom" : "split",  "split_after" : 20},
      	{ "nom" : "length", "nb_mois" : 50, "taux": 0.017, "taux_assurance": 0.001}
      ]
    },
    {
      "nom": "PRET3",
      "nominal": 40000,
      "debut": "2011-01-01",
      "echeances": [
        { "debut":   1, "fin":  168, "montant": 324, "taux": 0.04 }
        
      ],
      "assurances": [
        { "debut": 1, "fin": 168, "capital": 40000, "taux": 0.004 }
      ],
      "strategies":[
      	 { "nom" : "split",  "split_after" : 20},
      	{ "nom" : "lissage-mensualite", "mensualite_cible" : 730, "taux" : 0.040, "taux_assurance" : "0.004"}
      ]
    }
  ]
}
```

* pret.echeances : Séries d'échéances, chaque série définit un période allant du terme `début`au terme `fin`, pendant laquelle le taux `taux` est appliqué, et pendant laquelle les mensualités ont comme valeur `montant`
* pret.assurances : Assurances, Chaque assurance définit une période par `début` et `fin`, pendant laquelle le `capital` est assuré au taux `taux`
* pret.strategies : les stratégies permettent d'agir sur les paramètres du prêt, par example ajuster la durée pour obtenir une mensualité cible, ou d'effectuer un lissage. Après exécution des stratégies, un nouveau tableau d'amortissement est affiché.

Strategies
----------

* Lissage avec une mensualité cible : s'applique sur le dernier prêt, ajuste la durée du prêt pour obtenir des mensualités constantes pendant toute la durée du prêt, tous prêts confondus.
```
	{ "nom" : "lissage-mensualite", "mensualite_cible" : 678, "taux" : 0.0120, "taux_assurance" : "0.0027" }
```
* Lissage avec une durée cible : s'applique sur le dernier prêt, ajuste le montant des mensualités pour obtenir une durée cible en nombre de mois.
```
{ "nom" : "lissage-duree", "duree_cible" : 143, "taux" : 0.0135, "taux_assurance" : "0.0027" }
```
* Split : permet de travailler sur un tableau d'amortissement complet exisant, définit la mensualité après laquelle la durée et la mensualité peuvent varié, utile pour une renégociation de crédit pour laquelle les mensualité précédant la date de renégociation doivent rester inchangées. S'applique à tous les prêts concernés par la renégociation.
```
{ "nom" : "split",  "split_after" : 50 },
{ "nom" : "lissage-mensualite", "mensualite_cible" : 841, "taux" : 0.020, "taux_assurance" : "0.002"}
```
* Apport : Apport de capital, pour amotissement du crédit au terme précisé.
```
{ "nom" : "apport", "montant" : 3400, "terme" : 51},
```

