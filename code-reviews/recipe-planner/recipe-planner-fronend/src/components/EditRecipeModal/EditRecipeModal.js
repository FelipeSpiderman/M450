import React, {useState} from 'react';
import {Alert, Button, Col, Form, Modal, Row} from 'react-bootstrap';

const baseURL = "http://localhost:8080/api/recipes";
const units = ['PIECE', 'GRAMM', 'KILOGRAMM', 'LITRE', 'DECILITRE'];

let nextIngredientId = 1;

const createIngredient = (ingredient = {}) => ({
    key: `ingredient-${nextIngredientId++}`,
    name: ingredient.name ?? '',
    comment: ingredient.comment ?? '',
    unit: ingredient.unit ?? 'PIECE',
    amount: ingredient.amount ?? ''
});

const initialForm = (recipe) => ({
    name: recipe.name ?? '',
    description: recipe.description ?? '',
    imageUrl: recipe.imageUrl ?? '',
    ingredients: (recipe.ingredients ?? []).map(createIngredient)
});

const validateForm = (formData) => {
    const errors = {};

    if (!formData.name.trim()) {
        errors.name = 'Please enter a recipe name.';
    } else if (formData.name.trim().length > 80) {
        errors.name = 'The name must not exceed 80 characters.';
    }

    if (!formData.description.trim()) {
        errors.description = 'Please enter a description.';
    } else if (formData.description.trim().length > 500) {
        errors.description = 'The description must not exceed 500 characters.';
    }

    const imageUrl = formData.imageUrl.trim();
    if (imageUrl && !/^https?:\/\/.+/i.test(imageUrl)) {
        errors.imageUrl = 'Please enter a valid URL starting with http:// or https://.';
    }

    if (formData.ingredients.length === 0) {
        errors.ingredients = 'Add at least one ingredient.';
    } else if (formData.ingredients.some((ingredient) => {
        const amount = Number(ingredient.amount);
        return !ingredient.name.trim() || !Number.isInteger(amount) || amount < 1;
    })) {
        errors.ingredients = 'Every ingredient needs a name and a whole number greater than 0.';
    }

    return errors;
};

function EditRecipeModal({recipe, onClose, onSaved}) {
    const [formData, setFormData] = useState(() => initialForm(recipe));
    const [errors, setErrors] = useState({});
    const [saving, setSaving] = useState(false);
    const [serverError, setServerError] = useState('');

    const handleChange = (event) => {
        const {name, value} = event.target;
        setFormData((current) => ({...current, [name]: value}));
        setErrors((current) => ({...current, [name]: ''}));
    };

    const addIngredient = () => {
        setFormData((current) => ({
            ...current,
            ingredients: [...current.ingredients, createIngredient()]
        }));
        setErrors((current) => ({...current, ingredients: ''}));
    };

    const updateIngredient = (key, field, value) => {
        setFormData((current) => ({
            ...current,
            ingredients: current.ingredients.map((ingredient) =>
                ingredient.key === key ? {...ingredient, [field]: value} : ingredient
            )
        }));
        setErrors((current) => ({...current, ingredients: ''}));
    };

    const removeIngredient = (key) => {
        setFormData((current) => ({
            ...current,
            ingredients: current.ingredients.filter((ingredient) => ingredient.key !== key)
        }));
        setErrors((current) => ({...current, ingredients: ''}));
    };

    const handleSubmit = async (event) => {
        event.preventDefault();
        setServerError('');

        const validationErrors = validateForm(formData);
        if (Object.keys(validationErrors).length > 0) {
            setErrors(validationErrors);
            return;
        }

        setSaving(true);
        try {
            const response = await fetch(`${baseURL}/${recipe.id}`, {
                method: 'PUT',
                headers: {'Content-Type': 'application/json'},
                body: JSON.stringify({
                    name: formData.name.trim(),
                    description: formData.description.trim(),
                    imageUrl: formData.imageUrl.trim(),
                    ingredients: formData.ingredients.map((ingredient) => ({
                        name: ingredient.name.trim(),
                        comment: ingredient.comment,
                        unit: ingredient.unit,
                        amount: Number(ingredient.amount)
                    }))
                })
            });

            if (response.status === 404) {
                throw new Error('This recipe no longer exists.');
            }
            if (!response.ok) {
                throw new Error('Could not update the recipe. Please check your input.');
            }

            onSaved(await response.json());
        } catch (error) {
            setServerError(error.message || 'Could not update the recipe.');
        } finally {
            setSaving(false);
        }
    };

    return (
        <Modal
            show
            onHide={onClose}
            backdrop={saving ? 'static' : true}
            keyboard={!saving}
            size="lg"
            centered
        >
            <Form onSubmit={handleSubmit} noValidate>
                <Modal.Header closeButton={!saving}>
                    <Modal.Title>Edit Recipe</Modal.Title>
                </Modal.Header>

                <Modal.Body>
                    {serverError && <Alert variant="danger">{serverError}</Alert>}

                    <Form.Group className="mb-2" controlId="editName">
                        <Form.Label>Recipe Name:</Form.Label>
                        <Form.Control
                            name="name"
                            value={formData.name}
                            onChange={handleChange}
                            isInvalid={!!errors.name}
                        />
                        <Form.Control.Feedback type="invalid">{errors.name}</Form.Control.Feedback>
                    </Form.Group>

                    <Form.Group className="mb-2" controlId="editDescription">
                        <Form.Label>Description:</Form.Label>
                        <Form.Control
                            name="description"
                            value={formData.description}
                            onChange={handleChange}
                            isInvalid={!!errors.description}
                        />
                        <Form.Control.Feedback type="invalid">{errors.description}</Form.Control.Feedback>
                    </Form.Group>

                    <Form.Group className="mb-3" controlId="editImageUrl">
                        <Form.Label>Image URL:</Form.Label>
                        <Form.Control
                            name="imageUrl"
                            value={formData.imageUrl}
                            onChange={handleChange}
                            isInvalid={!!errors.imageUrl}
                        />
                        <Form.Control.Feedback type="invalid">{errors.imageUrl}</Form.Control.Feedback>
                    </Form.Group>

                    <Row>
                        <Col>Ingredient</Col>
                        <Col>Unit</Col>
                        <Col>Quantity</Col>
                        <Col xs={1}></Col>
                    </Row>
                    <hr/>

                    {formData.ingredients.map((ingredient) => (
                        <Row key={ingredient.key}>
                            <Col>
                                <Form.Group className="mb-1">
                                    <Form.Control
                                        value={ingredient.name}
                                        onChange={(event) => updateIngredient(ingredient.key, 'name', event.target.value)}
                                        placeholder="Name"
                                    />
                                </Form.Group>
                            </Col>
                            <Col>
                                <Form.Group className="mb-1">
                                    <Form.Select
                                        value={ingredient.unit}
                                        onChange={(event) => updateIngredient(ingredient.key, 'unit', event.target.value)}
                                    >
                                        {units.map((unit) => <option key={unit} value={unit}>{unit}</option>)}
                                    </Form.Select>
                                </Form.Group>
                            </Col>
                            <Col>
                                <Form.Group className="mb-1">
                                    <Form.Control
                                        type="number"
                                        min="1"
                                        step="1"
                                        value={ingredient.amount}
                                        onChange={(event) => updateIngredient(ingredient.key, 'amount', event.target.value)}
                                        placeholder="Quantity"
                                    />
                                </Form.Group>
                            </Col>
                            <Col xs={1}>
                                <Button
                                    type="button"
                                    variant="outline-dark"
                                    onClick={() => removeIngredient(ingredient.key)}
                                >x</Button>
                            </Col>
                        </Row>
                    ))}

                    {errors.ingredients &&
                        <Alert variant="warning" className="mt-2">{errors.ingredients}</Alert>}

                    <Button type="button" variant="warning" onClick={addIngredient}>
                        Add Ingredient
                    </Button>
                </Modal.Body>

                <Modal.Footer>
                    <Button variant="secondary" onClick={onClose} disabled={saving}>
                        Cancel
                    </Button>
                    <Button variant="primary" type="submit" disabled={saving}>
                        {saving ? 'Saving...' : 'Save Changes'}
                    </Button>
                </Modal.Footer>
            </Form>
        </Modal>
    );
}

export default EditRecipeModal;
