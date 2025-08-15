import { useState, useEffect } from "react";
import { api } from "./api";
import {
  Container,
  Typography,
  TextField,
  Button,
  Paper,
  Card,
  CardContent,
  Checkbox,
  FormControlLabel,
  IconButton,
  Box,
  Divider,
  Tooltip
} from "@mui/material";
import { DragDropContext, Droppable, Draggable } from "@hello-pangea/dnd";
import DeleteIcon from "@mui/icons-material/Delete";
import AddIcon from "@mui/icons-material/Add";

export default function App() {
  const [tasks, setTasks] = useState([]);
  const [title, setTitle] = useState("");
  const [description, setDescription] = useState("");
  const [dueDate, setDueDate] = useState("");

  const columns = {
    todo: { name: "To Do", color: "#ff9800" },
    done: { name: "Done", color: "#4caf50" },
  };

  useEffect(() => {
    fetchTasks();
  }, []);

  const fetchTasks = async () => {
    try {
      const res = await api.get("/tasks");
      setTasks(res.data);
    } catch (error) {
      console.error(error);
    }
  };

  const addTask = async () => {
    if (!title.trim()) return;
    try {
      await api.post("/tasks", {
        title,
        description,
        dueDate,
        status: false,
        position: tasks.length,
      });
      setTitle("");
      setDescription("");
      setDueDate("");
      fetchTasks();
    } catch (error) {
      console.error(error);
    }
  };

  const deleteTask = async (id) => {
    try {
      await api.delete(`/tasks/${id}`);
      fetchTasks();
    } catch (error) {
      console.error(error);
    }
  };

  const toggleTaskStatus = async (task) => {
    try {
      await api.put(`/tasks/${task.id}`, { ...task, status: !task.status });
      fetchTasks();
    } catch (error) {
      console.error(error);
    }
  };

  const onDragEnd = async (result) => {
    if (!result.destination) return;
    const reordered = Array.from(tasks);
    const [moved] = reordered.splice(result.source.index, 1);
    reordered.splice(result.destination.index, 0, moved);

    const updated = reordered.map((task, index) => ({
      ...task,
      position: index,
    }));

    setTasks(updated);
    try {
      await api.put("/tasks/reorder", updated);
    } catch (err) {
      console.error("Reorder failed", err);
      fetchTasks();
    }
  };

  const tasksByStatus = { todo: [], done: [] };
  tasks.forEach((t) => {
    if (t.status) tasksByStatus.done.push(t);
    else tasksByStatus.todo.push(t);
  });

  return (
    <Container sx={{ mt: 4 }}>
      <Typography variant="h3" align="center" gutterBottom fontWeight="bold">
        Task Manager Pro
      </Typography>

      {/* Task Input */}
      <Paper
        elevation={3}
        sx={{ p: 2, display: "flex", gap: 2, flexWrap: "wrap", mb: 3 }}
      >
        <TextField
          label="Title"
          value={title}
          onChange={(e) => setTitle(e.target.value)}
          size="small"
        />
        <TextField
          label="Description"
          value={description}
          onChange={(e) => setDescription(e.target.value)}
          size="small"
        />
        <TextField
          type="date"
          label="Due Date"
          value={dueDate}
          onChange={(e) => setDueDate(e.target.value)}
          InputLabelProps={{ shrink: true }}
          size="small"
        />
        <Button
          variant="contained"
          color="primary"
          startIcon={<AddIcon />}
          onClick={addTask}
        >
          Add
        </Button>
      </Paper>

      {/* Drag and Drop Columns */}
      <DragDropContext onDragEnd={onDragEnd}>
        <Box sx={{ display: "flex", gap: 2, alignItems: "flex-start" }}>
          {Object.entries(columns).map(([colId, col]) => (
            <Droppable droppableId={colId} key={colId}>
              {(provided) => (
                <Paper
                  ref={provided.innerRef}
                  {...provided.droppableProps}
                  sx={{
                    flex: 1,
                    minHeight: 500,
                    p: 2,
                    borderTop: `4px solid ${col.color}`,
                    backgroundColor: "#f9f9f9",
                  }}
                >
                  <Typography
                    variant="h6"
                    gutterBottom
                    fontWeight="bold"
                    sx={{ color: col.color }}
                  >
                    {col.name}
                  </Typography>
                  <Divider sx={{ mb: 2 }} />

                  {tasksByStatus[colId].map((task, index) => (
                    <Draggable
                      key={task.id}
                      draggableId={task.id.toString()}
                      index={index}
                    >
                      {(provided, snapshot) => (
                        <Card
                          ref={provided.innerRef}
                          {...provided.draggableProps}
                          {...provided.dragHandleProps}
                          sx={{
                            mb: 1.5,
                            p: 1,
                            transition: "0.2s",
                            boxShadow: snapshot.isDragging
                              ? "0 4px 12px rgba(0,0,0,0.2)"
                              : "none",
                          }}
                        >
                          <CardContent sx={{ pb: "8px !important" }}>
                            <FormControlLabel
                              control={
                                <Checkbox
                                  checked={task.status}
                                  onChange={() => toggleTaskStatus(task)}
                                />
                              }
                              label={
                                <Typography
                                  variant="subtitle1"
                                  sx={{
                                    textDecoration: task.status
                                      ? "line-through"
                                      : "none",
                                    fontWeight: "bold",
                                  }}
                                >
                                  {task.title}
                                </Typography>
                              }
                            />
                            <Typography variant="body2" color="text.secondary">
                              {task.description}
                            </Typography>
                            <Typography
                              variant="caption"
                              color="text.secondary"
                            >
                              Due: {task.dueDate}
                            </Typography>
                          </CardContent>
                          <Tooltip title="Delete Task">
                            <IconButton
                              color="error"
                              onClick={() => deleteTask(task.id)}
                              size="small"
                            >
                              <DeleteIcon fontSize="small" />
                            </IconButton>
                          </Tooltip>
                        </Card>
                      )}
                    </Draggable>
                  ))}
                  {provided.placeholder}
                </Paper>
              )}
            </Droppable>
          ))}
        </Box>
      </DragDropContext>
    </Container>
  );
}
