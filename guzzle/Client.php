<?php
/**
 * Created by PhpStorm.
 * User: pierre
 * Date: 27/05/2018
 * Time: 17:46
 */
require 'vendor/autoload.php';
use GuzzleHttp\Client;

// Init Guzzle client
$client = new Client();

/*
 *
 * TEST DU SERVICE APPMANAGER
 *
 */

echo "TEST DU SERVICE APPMANAGER : \n";

// Affichage de la liste de toute les réponse

$result = $client->request('GET', "http://calm-cliffs-46267.herokuapp.com/approval");
$approvals = (array) json_decode($result->getBody());
echo "Liste des réponses :";
var_dump($approvals);

// Récupération de la réponse correspondant au compte bancaire ABC
$result = $client->request('GET', "http://calm-cliffs-46267.herokuapp.com/approval/ABC");
$approval = (array) json_decode($result->getBody());

// Affichage de la réponse
var_dump($approval);

// Modification de la réponse précédente
if (isset($approval['accepte'])) {
    $approval['accepte'] = !$approval['accepte'];
    echo $approval['accepte'];
} else {
    print_r('La réponse ne contient pas de booléen accepte');
}

$result = $client->request('PATCH', "http://calm-cliffs-46267.herokuapp.com/approval", ['json' => $approval]);
var_dump(json_decode($result->getBody()));

// Suppression de la réponse correspondant au compte bancaire ABC
$result = $client->request('DELETE', "http://calm-cliffs-46267.herokuapp.com/approval/ABC");
if ($result->getStatusCode()) {
    echo "Suppression de la réponse ABC réalisée avec succès.\n";
}

// Ajout de la réponse correspondant au compte bancaire ABC
$result = $client->request('POST', "http://calm-cliffs-46267.herokuapp.com/approval", ['json' => $approval]);
if ($result->getStatusCode()) {
    echo "Ajout de la réponse ABC réalisée avec succès.\n";
}

// Affichage de la liste de toute les réponse

$result = $client->request('GET', "http://calm-cliffs-46267.herokuapp.com/approval");
$approvals = (array) json_decode($result->getBody());
echo "Liste des réponses :";
var_dump($approvals);

/*
 *
 * TEST DU SERVICE ACCMANAGER
 *
 */

echo "\nTEST DU SERVICE ACCMANAGER : \n";

// Affichage de la liste de tous les comptes

$result = $client->request('GET', "http://calm-cliffs-46267.herokuapp.com/account");
$accounts = (array) json_decode($result->getBody());
echo "Liste des comptes :";
var_dump($approvals);

// Récupération du compte bancaire ABC
$result = $client->request('GET', "http://calm-cliffs-46267.herokuapp.com/account/ABC");
$account = (array) json_decode($result->getBody());

// Affichage du compte
var_dump($account);

// Modification de la somme du compte ABC

$result = $client->request('POST', "http://calm-cliffs-46267.herokuapp.com/account/ABC/500");

if ($result->getStatusCode() == 200) {
    echo "Modification de la somme du compte ABC réalisée avec succès.\n";
}

// Suppression du compte bancaire ABC
$result = $client->request('DELETE', "http://calm-cliffs-46267.herokuapp.com/account/ABC");
if ($result->getStatusCode() == 200) {
    echo "Suppression de la réponse ABC réalisée avec succès.\n";
}

// Ajout du compte bancaire ABC

$result = $client->request('POST', "http://calm-cliffs-46267.herokuapp.com/account?idCompte=ABC&nom=TEST&prenom=Test&somme=1000&risk=false");
if ($result->getStatusCode() == 200) {
    echo "Ajout de la réponse ABC réalisée avec succès.\n";
}

// Affichage de la liste de toute les réponse

$result = $client->request('GET', "http://calm-cliffs-46267.herokuapp.com/approval");
$approvals = (array) json_decode($result->getBody());

echo "Liste des réponses :";
var_dump($approvals);

/*
 *
 * TEST DU SERVICE CHECKACCOUNT
 *
 */

echo "\nTEST DU SERVICE CHECKACCOUNT\n";

$result = $client->request('GET', "http://calm-cliffs-46267.herokuapp.com/checkAccount/ABC");

if ($result->getBody() == true) {
    echo "Il y a un risque pour le compte ABC\n";
}  else {
    echo "Il n'y a pas de risque pour le compte ABC\n";
}

/*
 *
 * TEST DU SERVICE LOANAPPROVAL
 *
 */

echo "\nTEST DU SERVICE LOANAPPROVAL\n";


$result = $client->request('GET', "http://1-dot-inf63app5.appspot.com/approval?idCompte=ABC&somme=444");
$result = (array) json_decode($result->getBody()->getContents());

echo "Réponse d'une demande de crédit de 444€ sur un compte n'ayant pas de risque : \n";
echo($result['message'] . "\n");

$result = $client->request('GET', "http://1-dot-inf63app5.appspot.com/approval?idCompte=AB13&somme=232");
$result = (array) json_decode($result->getBody()->getContents());

echo "Réponse d'une demande de crédit de 232€ sur un compte ayant un risque (avec approval accepte): \n";
echo($result['message'] . "\n");

$result = $client->request('GET', "http://1-dot-inf63app5.appspot.com/approval?idCompte=ABC&somme=12000");
$result = (array) json_decode($result->getBody()->getContents());

echo "Réponse d'une demande de crédit de 12000€ sur un compte n'ayant pas de risque : \n";
echo($result['message'] . "\n");

$result = $client->request('GET', "http://1-dot-inf63app5.appspot.com/approval?idCompte=AB13&somme=12000");
$result = (array) json_decode($result->getBody()->getContents());

echo "Réponse d'une demande de crédit de 12000€ sur un compte ayant un risque (avec approval accepte): \n";
echo($result['message'] . "\n");

$result = $client->request('GET', "http://1-dot-inf63app5.appspot.com/approval?idCompte=AB133&somme=12000");
$result = (array) json_decode($result->getBody()->getContents());

echo "Réponse d'une demande de crédit de 12000€ sur un compte ayant un risque (avec approval refusee): \n";
echo($result['message'] . "\n");
