import React, {useEffect, useState} from 'react';
import './Browse.css';
import axios from "axios";
import Recipe from "../Recipe/Recipe";
import {Alert, Col, Row} from "react-bootstrap";
import EditRecipeModal from "../EditRecipeModal/EditRecipeModal";

const baseURL = "http://localhost:8080/api/recipes";


const Browse = () => {

        const [recipes, setRecipes] = useState(null);
        const [editingRecipe, setEditingRecipe] = useState(null);
        const [success, setSuccess] = useState('');

        useEffect(() => {
            axios.get(baseURL).then((response) => {
                setRecipes(response.data);
            });
        }, []);

        const handleSaved = (updatedRecipe) => {
            setRecipes((current) => current.map((recipe) =>
                recipe.id === updatedRecipe.id ? updatedRecipe : recipe
            ));
            setEditingRecipe(null);
            setSuccess(`"${updatedRecipe.name}" was updated.`);
        };

        if (!recipes) return null;

        return (
            <>
                {success &&
                    <Alert variant="success" dismissible onClose={() => setSuccess('')}>
                        {success}
                    </Alert>}
                <Row>
                    {recipes.map((recipe) => (
                        <Col key={recipe.id} sm={12} md={6} lg={4} xl={3}>
                            <Recipe
                                id={recipe.id}
                                title={recipe.name}
                                description={recipe.description}
                                image={recipe.imageUrl}
                                onEdit={() => setEditingRecipe(recipe)}
                            />
                        </Col>
                    ))}
                </Row>
                {editingRecipe &&
                    <EditRecipeModal
                        recipe={editingRecipe}
                        onClose={() => setEditingRecipe(null)}
                        onSaved={handleSaved}
                    />}
            </>
        );
}


Browse.propTypes = {};

Browse.defaultProps = {};

export default Browse;
