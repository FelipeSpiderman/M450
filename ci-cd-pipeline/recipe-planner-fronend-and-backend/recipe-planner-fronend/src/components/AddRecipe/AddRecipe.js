import React, {useEffect, useRef, useState} from 'react';
import './AddRecipe.css';
import {useForm} from "react-hook-form";
import {Alert, Button, Col, Form, Row} from 'react-bootstrap';
import {useNavigate} from 'react-router-dom';
import axios from "axios";
import AddIngredient from "../AddIngredient/AddIngredient";

const baseURL = "http://localhost:8080/api/recipes";

function AddRecipe() {
    const navigate = useNavigate();
    const redirectTimer = useRef(null);
    const [ingredients, setIngredients] = useState([]);
    const [listId, setListId] = useState(1);
    const [serverError, setServerError] = useState('');
    const [success, setSuccess] = useState('');

    const {
        register,
        handleSubmit,
        reset,
        setError,
        clearErrors,
        formState: {errors, isSubmitting},
    } = useForm();

    useEffect(() => () => clearTimeout(redirectTimer.current), []);

    const addIngredient = () => {
        clearErrors('ingredients');
        setIngredients([...ingredients, {
            listId,
            name: '',
            comment: '',
            unit: 'PIECE',
            amount: ''
        }]);
        setListId(listId + 1);
    };

    const updateIngredient = (ingredientObj) => {
        clearErrors('ingredients');
        setIngredients(ingredients.map((ingredient) =>
            ingredient.listId === ingredientObj.listId ? ingredientObj : ingredient
        ));
    };

    const removeIngredient = (ingredientObj) => {
        clearErrors('ingredients');
        setIngredients(ingredients.filter((ingredient) => ingredient.listId !== ingredientObj.listId));
    };

    const onSubmit = async (data) => {
        setServerError('');
        clearErrors('ingredients');

        if (ingredients.length === 0) {
            setError('ingredients', {type: 'manual', message: 'Add at least one ingredient.'});
            return;
        }

        const hasInvalidIngredient = ingredients.some((ingredient) => {
            const amount = Number(ingredient.amount);
            return !ingredient.name.trim() || !Number.isInteger(amount) || amount < 1;
        });

        if (hasInvalidIngredient) {
            setError('ingredients', {
                type: 'manual',
                message: 'Every ingredient needs a name and a whole number greater than 0.'
            });
            return;
        }

        const recipe = {
            name: data.name.trim(),
            description: data.description.trim(),
            imageUrl: data.imageUrl.trim(),
            ingredients: ingredients.map((ingredient) => ({
                name: ingredient.name.trim(),
                comment: ingredient.comment,
                unit: ingredient.unit,
                amount: Number(ingredient.amount)
            }))
        };

        try {
            await axios.post(baseURL, recipe);
            setSuccess('Recipe saved. Redirecting to the overview...');
            reset();
            setIngredients([]);
            redirectTimer.current = setTimeout(() => navigate('/'), 1200);
        } catch (error) {
            setServerError('Could not save the recipe. Please check your input and try again.');
        }
    };

    const renderIngredients = ingredients.map((ingredient) => (
        <AddIngredient
            key={ingredient.listId}
            ingredient={ingredient}
            updateIngredient={updateIngredient}
            removeIngredient={removeIngredient}
        />
    ));

    return (
        <>
            <div className="bg">
                <div className="m-3">
                    <h1 className="h3 bg-dark text-bg-primary mt-2">Add Recipe</h1>

                    {success && <Alert variant="success">{success}</Alert>}
                    {serverError && <Alert variant="danger">{serverError}</Alert>}

                    <Form onSubmit={handleSubmit(onSubmit)} noValidate>
                        <Form.Group className="mb-1" controlId="formBasicName">
                            <Form.Label>Recipe Name:</Form.Label>
                            <Form.Control
                                placeholder="Name"
                                isInvalid={!!errors.name}
                                {...register('name', {
                                    required: 'Please enter a recipe name.',
                                    maxLength: {value: 80, message: 'The name must not exceed 80 characters.'}
                                })}
                            />
                            <Form.Control.Feedback type="invalid">{errors.name?.message}</Form.Control.Feedback>
                        </Form.Group>

                        <Form.Group className="mb-1" controlId="formBasicDescription">
                            <Form.Label>Description:</Form.Label>
                            <Form.Control
                                placeholder="Description"
                                isInvalid={!!errors.description}
                                {...register('description', {
                                    required: 'Please enter a description.',
                                    maxLength: {value: 500, message: 'The description must not exceed 500 characters.'}
                                })}
                            />
                            <Form.Control.Feedback type="invalid">{errors.description?.message}</Form.Control.Feedback>
                        </Form.Group>

                        <Form.Group className="mb-1 mb-5" controlId="formBasicImageUrl">
                            <Form.Label>Image URL:</Form.Label>
                            <Form.Control
                                placeholder="URL"
                                isInvalid={!!errors.imageUrl}
                                {...register('imageUrl', {
                                    validate: (value) => !value.trim() || /^https?:\/\/.+/i.test(value.trim())
                                        || 'Please enter a valid URL starting with http:// or https://.'
                                })}
                            />
                            <Form.Control.Feedback type="invalid">{errors.imageUrl?.message}</Form.Control.Feedback>
                        </Form.Group>

                        <Row>
                            <Col>Ingredient</Col>
                            <Col>Unit</Col>
                            <Col>Quantity</Col>
                            <Col xs={1}></Col>
                        </Row>
                        <hr/>
                        {renderIngredients}

                        {errors.ingredients &&
                            <Alert variant="warning" className="mt-2">{errors.ingredients.message}</Alert>}

                        <Button
                            type="button"
                            variant='warning'
                            onClick={addIngredient}
                            className="mt-1"
                        >Add Ingredient</Button>

                        <Button
                            variant="primary"
                            type="submit"
                            className="mb-5"
                            disabled={isSubmitting || !!success}
                        >
                            {isSubmitting ? 'Saving...' : 'Submit'}
                        </Button>
                    </Form>
                </div>
            </div>
        </>
    );
}

export default AddRecipe;
